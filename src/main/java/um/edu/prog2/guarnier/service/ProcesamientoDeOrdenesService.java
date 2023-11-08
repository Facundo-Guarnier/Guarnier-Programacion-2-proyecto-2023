package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class ProcesamientoDeOrdenesService {

    public List<OrdenDTO> ordenesProcesadas = new ArrayList<OrdenDTO>();
    public List<OrdenDTO> ordenesFallidas = new ArrayList<OrdenDTO>();
    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Autowired
    CatedraAPIService cs;

    @Autowired
    OrdenService ordenService;

    @Autowired
    ReportarOperacionesService ros;

    @PostConstruct
    public void init() {
        log.info("Iniciando 'ProcesamientoDeOrdenesService'");

        //! Funcion, retraso inicial, intervalo de ejecución (1440 minutos = 24 horas), unidad de tiempo
        scheduler.scheduleAtFixedRate(
            () -> {
                List<List<OrdenDTO>> r = analizarOrdenes();
            },
            10000,
            10,
            TimeUnit.SECONDS
        );
    }

    //! Método que tiene que leer la DB, analizar las ordenes y devolver 2 listas con las procesadas y las fallidas.
    public List<List<OrdenDTO>> analizarOrdenes() {
        this.ordenesProcesadas.clear();
        this.ordenesFallidas.clear();

        log.debug("Analizando ordenes");
        try {
            ordenService
                .findPendientes()
                .forEach(orden -> {
                    log.debug("Procesando ordenes instantáneas: " + orden);
                    if (this.puedeRealizarOperacion(orden)) {
                        esPosibleOperar(orden);
                    } else {
                        noEsPosibleOperar(orden);
                    }
                });
        } catch (Exception e) {
            log.error("Error al buscar ordenes en DB y analizarlas.", e);
        }

        //! Devuelve una lista de listas, la primera con las ordenes procesadas y la segunda con las fallidas
        List<List<OrdenDTO>> resultado = new ArrayList<>();
        resultado.add(ordenesProcesadas);
        resultado.add(ordenesFallidas);

        this.reportar(ordenesProcesadas, ordenesFallidas);

        return resultado;
    }

    //! Revisa si la orden puede realizarse.
    public boolean puedeRealizarOperacion(OrdenDTO orden) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime fechaHora = ZonedDateTime.parse(orden.getFechaOperacion(), formatter);
        ZoneId zonaHoraria = ZoneId.of("UTC");
        ZonedDateTime fechaHoraLocal = fechaHora.withZoneSameInstant(zonaHoraria);
        int hora = fechaHoraLocal.getHour();

        //T* Condiciones:
        //! 1• Una orden instantánea no puede ejecutarse fuera del horario de transacciones,
        //!    antes de las 09:00 y después de las 18:00.
        if ("AHORA".equals(orden.getModo()) && hora <= 9 || hora > 18) {
            log.debug("La hora está fuera del rango de 9:00 AM y 6:00 PM para la orden " + orden.getId() + " inmediata. Hora:" + hora);
            orden.setEstado("FALLIDO - HORA FUERA DE RANGO");
            return false;
        }

        //! 2• Una orden debe tener asociado un cliente y una acción de una compañía. Se debe
        //!    verificar que el Id de cliente y el Id de la acción sean válidos. Para esto
        //!    se debe consultar el servicio cátedra buscando por Id de ambos.
        if (orden.getCliente() == null || orden.getAccionId() == null) {
            log.debug("La orden " + orden.getId() + " no tiene un cliente o una acción asociada.");
            orden.setEstado("FALLIDO - SIN CLIENTE O ACCION ASOCIADA");
            return false;
        }

        //! Cliente ID
        String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar";
        JsonNode respuestaCliente = this.cs.getConJWT(urlCliente);
        JsonNode clientes = respuestaCliente.get("clientes");
        boolean clienteValido = false;

        for (JsonNode cliente : clientes) {
            int id = cliente.get("id").asInt();
            if (id == orden.getCliente()) {
                clienteValido = true;
                break;
            }
        }

        if (!clienteValido) {
            log.debug("El cliente asociado a la orden " + orden.getId() + " no es válido: " + orden.getCliente());
            orden.setEstado("FALLIDO - CLIENTE NO VALIDO");
            return false;
        }

        //! Acción ID
        String urlAccion = "http://192.168.194.254:8000/api/acciones/buscar";
        boolean accionValida = false;
        JsonNode respuestaAccion = this.cs.getConJWT(urlAccion);
        JsonNode acciones = respuestaAccion.get("acciones");

        for (JsonNode accion : acciones) {
            int accionId = accion.get("id").asInt();
            String empresa = accion.get("codigo").asText();
            if (accionId == orden.getAccionId() && empresa.equals(orden.getAccion())) {
                accionValida = true;
                break;
            }
        }

        if (!accionValida) {
            log.debug("La acción asociada a la orden " + orden.getId() + " no es válida: " + orden.getAccionId());
            orden.setEstado("FALLIDO - ACCION ID Y ACCION NO VALIDOS");
            return false;
        }

        //! 3• Una orden no puede tener un número de acciones <=0.
        if (orden.getCantidad() <= 0) {
            log.debug("La cantidad de acciones de la orden " + orden.getId() + " es menor o igual a 0.");
            orden.setEstado("FALLIDO - CANTIDAD DE ACCIONES MENOR O IGUAL A 0");
            return false;
        }

        //! 4• Revisar los valores del atributo MODO
        if (!"AHORA".equals(orden.getModo()) && !"FINDIA".equals(orden.getModo()) && !"PRINCIPIODIA".equals(orden.getModo())) {
            log.debug("El modo de la orden " + orden.getId() + " no es válido: " + orden.getModo());
            orden.setEstado("FALLIDO - MODO NO VALIDO");
            return false;
        }

        //! 5• Revisar los valores del atributo OPERACION
        if (!"COMPRA".equals(orden.getOperacion()) && !"VENTA".equals(orden.getOperacion())) {
            log.debug("La operación " + orden.getId() + " de la orden no es válida: " + orden.getOperacion());
            orden.setEstado("FALLIDO - OPERACION NO VALIDA");
            return false;
        }

        //! Si todo está bien, devuelve true.
        orden.setEstado("PUEDE OPERAR");
        return true;
    }

    //! Para cuando no puede realizarse la operación.
    public void noEsPosibleOperar(OrdenDTO orden) {
        log.debug("No es posible realizar la operacion");
        this.ordenesFallidas.add(orden);
        ordenService.update(orden);
    }

    //! Para cuando puede realizarse la operación.
    public void esPosibleOperar(OrdenDTO orden) {
        log.debug("Es posible realizar la operacion " + orden.getId());

        if (!orden.getModo().equals("AHORA")) {
            programarOrden(orden);
        } else if (orden.getOperacion().equals("COMPRA")) {
            comprarOrden(orden);
        } else if (orden.getOperacion().equals("VENTA")) {
            venderOrden(orden);
        }

        this.ordenesProcesadas.add(orden);
    }

    //! Programar la orden.
    public void programarOrden(OrdenDTO orden) {
        log.debug("Programando operacion");
        orden.setEstado("PROGRAMADO");
        ordenService.update(orden);
    }

    //! Comprar la orden.
    public boolean venderOrden(OrdenDTO orden) {
        log.debug("Vendiendo orden");
        orden.setEstado("COMPLETADO");
        ordenService.update(orden);
        return true;
    }

    //! Vender la orden.
    public boolean comprarOrden(OrdenDTO orden) {
        log.debug("Comprando orden");
        orden.setEstado("COMPLETADO");
        ordenService.update(orden);
        return true;
    }

    //! Reportar las operaciones.
    private void reportar(List<OrdenDTO> ordenesProcesadas2, List<OrdenDTO> ordenesFallidas2) {
        ros.reportarOperaciones(ordenesProcesadas2, ordenesFallidas2);
    }
}

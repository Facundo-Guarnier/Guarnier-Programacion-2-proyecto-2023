package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.domain.Orden;

@Service
@Transactional
public class ProcesamientoDeOrdenesService {

    private List<Orden> ordenesProcesadas = new ArrayList<Orden>();
    private List<Orden> ordenesFallidas = new ArrayList<Orden>();
    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);

    @Autowired
    CatedraAPIService cs;

    @Autowired
    OrdenService ordenService;

    //! Método que tiene que leer la DB, analizar las ordenes y devolver 2 listas con las procesadoas y las fallidas.
    public List<List<Orden>> analizarOrdenes2() {
        log.debug("Analizando ordenes2");

        try {
            ordenService
                .findPendientes()
                .forEach(orden -> {
                    System.out.println("----- Procesamiento -----\n" + orden);
                    // if (this.puedeRealizarOperacion(orden)) {
                    //     esPosibleOperar(orden);
                    // } else {
                    //     noEsPosibleOperar(orden);
                    // }
                });
        } catch (Exception e) {
            log.error("Error al buscar ordenes en DB y analizarlas.", e);
        }

        return null;
        //! Devuelve una lista de listas, la primera con las ordenes procesadas y la segunda con las fallidas
        // List<List<Orden>> resultado = new ArrayList<>();
        // resultado.add(ordenesProcesadas);
        // resultado.add(ordenesFallidas);

        // this.reportar(resultado);

        // return resultado;
    }

    public List<List<Orden>> analizarOrdenes(List<Orden> ordenes) {
        log.debug("Analizando ordenes");
        try {
            for (Orden orden : ordenes) {
                if (this.puedeRealizarOperacion(orden)) {
                    esPosibleOperar(orden);
                } else {
                    noEsPosibleOperar(orden);
                }
            }
        } catch (Exception e) {
            log.error("Error al analizar las ordenes", e);
        }

        //! Devuelve una lista de listas, la primera con las ordenes procesadas y la segunda con las fallidas
        List<List<Orden>> resultado = new ArrayList<>();
        resultado.add(ordenesProcesadas);
        resultado.add(ordenesFallidas);

        this.reportar(resultado);

        return resultado;
    }

    public boolean puedeRealizarOperacion(Orden orden) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        ZonedDateTime fechaHora = ZonedDateTime.parse(orden.getFechaOperacion(), formatter);
        ZoneId zonaHoraria = ZoneId.of("UTC");
        ZonedDateTime fechaHoraLocal = fechaHora.withZoneSameInstant(zonaHoraria);
        int hora = fechaHoraLocal.getHour();

        //! Condiciones:
        //! 1• Una orden instantánea no puede ejecutarse fuera del horario de transacciones,
        //!    antes de las 09:00 y después de las 18:00.
        if ("AHORA".equals(orden.getModo()) && hora <= 9 || hora > 18) {
            log.debug("La hora está fuera del rango de 9:00 AM y 6:00 PM para una orden inmediata. Hora:" + hora);
            return false;
        }

        //! 2• Una orden debe tener asociado un cliente y una acción de una compañía. Se debe
        //!    verificar que el Id de cliente y el Id de la acción sean válidos. Para esto
        //!    se debe consultar el servicio cátedra buscando por Id de ambos.
        if (orden.getCliente() == null || orden.getAccionId() == null) {
            log.debug("La orden no tiene un cliente o una acción asociada.");
            return false;
        }

        //! Cliente ID
        // String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar?id=" + this.cliente;
        String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar?nombre=" + "Corvalan";

        JsonNode respuestaCliente = this.cs.getConJWT(urlCliente);

        JsonNode clientes = respuestaCliente.get("clientes");
        if (clientes.isArray() && clientes.size() > 0) {
            JsonNode cliente = clientes.get(0); // El primer cliente de la lista
            int id = cliente.get("id").asInt();
            if (id != orden.getCliente()) {
                log.debug("El cliente asociado a la orden no es válido. Cliente: " + id + " Orden cliente: " + orden.getCliente());
                return false;
            }
        } else {
            log.debug("El cliente asociado a la orden no es válido.");
            return false;
        }

        //! Acción ID
        // String urlAccion = "http://192.168.194.254:8000/api/acciones/buscar?id=" + this.accionId;
        String urlAccion = "http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion();

        JsonNode respuestaAccion = this.cs.getConJWT(urlAccion);

        JsonNode acciones = respuestaAccion.get("acciones");

        if (acciones.isArray() && acciones.size() > 0) {
            JsonNode accion = acciones.get(0); // La primera acción de la lista
            int id = accion.get("id").asInt();
            if (id != orden.getAccionId()) {
                log.debug("La acción asociada a la orden no es válida. Acción: " + id + " Orden accion: " + orden.getAccionId());
                return false;
            }
        } else {
            log.debug("La acción asociada a la orden no es válida.");
            return false;
        }

        //! 3• Una orden no puede tener un número de acciones <=0. Para verificar este punto
        //!    se deberá hacer una consulta a servicios de la cátedra.
        // JsonNode respuesta = this.solicitudHTTP("http://192.168.194.254:8000/api/acciones/buscar?id=4");
        if (orden.getCantidad() <= 0) {
            log.debug("La cantidad de acciones de la orden es menor o igual a 0.");
            return false;
        }

        //! 4• Revisar los valores del atributo MODO
        if (!"AHORA".equals(orden.getModo()) && !"FINDIA".equals(orden.getModo()) && !"PRINCIPIODIA".equals(orden.getModo())) {
            log.debug("El modo de la orden no es válido: " + orden.getModo());
            return false;
        }

        //! Si todo está bien, devuelve true.
        return true;
    }

    private void reportar(List<List<Orden>> resultado) {
        ReportarOperacionesService ros = new ReportarOperacionesService();
        // ros.reportarOperaciones(resultado);
    }

    //! Se almacenará la operación en una lista de operaciones fallidas y continuamos con la siguiente.
    public void noEsPosibleOperar(Orden orden) {
        log.debug("No es posible realizar la operacion");
        orden.setEstado("FALLIDO");
        this.ordenesFallidas.add(orden);
    }

    public void esPosibleOperar(Orden orden) {
        log.debug("Es posible realizar la operacion");

        if (!orden.getModo().equals("AHORA")) {
            programarOrden(orden);
        } else if (orden.getOperacion().equals("COMPRA")) {
            comprarOrden(orden);
        } else if (orden.getOperacion().equals("VENTA")) {
            venderOrden(orden);
        }

        this.ordenesProcesadas.add(orden);
    }

    //TODO ¿Que hay que hacer acá?
    public void programarOrden(Orden orden) {
        log.debug("Programando operacion");
        orden.setEstado("PROGRAMADO");
    }

    public boolean venderOrden(Orden orden) {
        log.debug("Vendiendo orden");
        orden.setEstado("COMPLETADO");
        return true;
    }

    public boolean comprarOrden(Orden orden) {
        log.debug("Comprando orden");
        orden.setEstado("COMPLETADO");
        return true;
    }
}

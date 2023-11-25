package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.exception.FalloConexionCatedraException;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class VerificadorDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(VerificadorDeOrdenesService.class);

    @Autowired
    CatedraAPIService cs;

    //! Revisa si la orden puede realizarse.
    public boolean puedeRealizarOperacion(OrdenDTO orden) throws FalloConexionCatedraException {
        //T* Condiciones:
        if (!instantáneas(orden)) {
            return false;
        }

        if (!clienteAccionAsociados(orden)) {
            return false;
        }

        if (!cantidadNula(orden)) {
            return false;
        }

        if (!modo(orden)) {
            return false;
        }

        if (!operacion(orden)) {
            return false;
        }

        if ("VENTA".equals(orden.getOperacion()) && "AHORA".equals(orden.getModo())) {
            if (!cantidadAcciones(orden)) {
                return false;
            }
        }

        //! Si todo está bien, devuelve true.
        return true;
    }

    //! 1.0 • Una orden instantánea no puede ejecutarse fuera del horario de transacciones,
    //!       antes de las 09:00 y después de las 18:00.
    public boolean instantáneas(OrdenDTO orden) {
        Instant fechaOperacionInstant = Instant.parse(orden.getFechaOperacion());
        ZoneId zonaHoraria = ZoneId.of("UTC");
        ZonedDateTime fechaHoraLocal = fechaOperacionInstant.atZone(zonaHoraria);
        Integer hora = fechaHoraLocal.getHour();

        if ("AHORA".equals(orden.getModo()) && hora < 9 || hora > 18) {
            log.debug("La hora está fuera del rango de 9:00 AM y 6:00 PM para la orden " + orden.getId() + " inmediata. Hora:" + hora);
            orden.setEstado(1);
            orden.setDescripcion("HORA FUERA DE RANGO");
            return false;
        }
        return true;
    }

    //! 2.0 • Una orden debe tener asociado un cliente y una acción de una compañía. Se debe
    //!       verificar que el Id de cliente y el Id de la acción sean válidos. Para esto
    //!       se debe consultar el servicio cátedra buscando por Id de ambos.
    public boolean clienteAccionAsociados(OrdenDTO orden) throws FalloConexionCatedraException {
        if (orden.getCliente() == null || orden.getAccionId() == null) {
            log.debug("La orden " + orden.getId() + " no tiene un cliente o una acción asociada.");
            orden.setEstado(1);
            orden.setDescripcion("SIN CLIENTE O ACCION ASOCIADA");
            return false;
        }

        //! Cliente ID
        String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar";
        JsonNode respuestaCliente = this.cs.getConJWT(urlCliente);
        if (respuestaCliente == null) {
            log.debug("No se pudo obtener la respuesta del servicio cátedra para buscar los clientes.");
            orden.setEstado(0);
            throw new FalloConexionCatedraException("No se pudo obtener la respuesta del servicio cátedra para buscar los clientes.");
        }
        JsonNode clientes = respuestaCliente.get("clientes");
        boolean clienteValido = false;

        for (JsonNode cliente : clientes) {
            int id = cliente.get("id").asInt();
            if (id == orden.getCliente()) {
                clienteValido = true;
                orden.setclienteNombre(cliente.get("nombreApellido").asText());
                break;
            }
        }

        if (!clienteValido) {
            log.debug("El cliente asociado a la orden " + orden.getId() + " no es válido: " + orden.getCliente());
            orden.setEstado(1);
            orden.setDescripcion("CLIENTE NO VALIDO");
            return false;
        }

        //! Acción ID
        String urlAccion = "http://192.168.194.254:8000/api/acciones/buscar";
        boolean accionValida = false;
        JsonNode respuestaAccion = this.cs.getConJWT(urlAccion);
        if (respuestaAccion == null) {
            log.debug("No se pudo obtener la respuesta del servicio cátedra para buscar las acciones.");
            orden.setEstado(0);
            throw new FalloConexionCatedraException("No se pudo obtener la respuesta del servicio cátedra para buscar las acciones.");
        }
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
            orden.setEstado(1);
            orden.setDescripcion("ACCION ID Y ACCION NO VALIDOS");
            return false;
        }
        return true;
    }

    //! 3.0 • Una orden no puede tener un número de acciones =0.
    public boolean cantidadNula(OrdenDTO orden) throws FalloConexionCatedraException {
        if (orden.getCantidad() <= 0) {
            log.debug("La cantidad de acciones de la orden " + orden.getId() + " es menor o igual a 0.");
            orden.setEstado(1);
            orden.setDescripcion("CANTIDAD DE ACCIONES MENOR O IGUAL A 0");
            return false;
        }
        return true;
    }

    //! 3.1 • Si la operación es de tipo VENTA y modo AHORA, se debe verificar que el cliente tenga la cantidad de acciones necesarias.
    public boolean cantidadAcciones(OrdenDTO orden) throws FalloConexionCatedraException {
        String urlClienteAccion =
            "http://192.168.194.254:8000/api/reporte-operaciones/consulta_cliente_accion?clienteId=" +
            orden.getCliente() +
            "&accionId=" +
            orden.getAccionId();
        boolean clienteAccionValida = false;
        JsonNode respuestaClienteAccion = this.cs.getConJWT(urlClienteAccion);

        if (respuestaClienteAccion == null) {
            log.debug("No se pudo obtener la respuesta del servicio cátedra para buscar las acciones del cliente.");
            orden.setEstado(0);
            throw new FalloConexionCatedraException("No se pudo obtener la respuesta del servicio cátedra para buscar las acciones.");
        }

        Integer cantidad = respuestaClienteAccion.get("cantidadActual").asInt();
        if (cantidad >= orden.getCantidad()) {
            clienteAccionValida = true;
        }

        if (!clienteAccionValida) {
            log.debug("El cliente " + orden.getCliente() + " no tiene la cantidad de acciones necesarias para la orden " + orden.getId());
            orden.setEstado(1);
            orden.setDescripcion("CANTIDAD DE ACCIONES INSUFICIENTE");
            return false;
        }

        return true;
    }

    //! 4.0 • Revisar los valores del atributo MODO
    public boolean modo(OrdenDTO orden) {
        if (!"AHORA".equals(orden.getModo()) && !"FINDIA".equals(orden.getModo()) && !"PRINCIPIODIA".equals(orden.getModo())) {
            log.debug("El modo de la orden " + orden.getId() + " no es válido: " + orden.getModo());
            orden.setEstado(1);
            orden.setDescripcion("MODO NO VALIDO");
            return false;
        }
        return true;
    }

    //! 5.0 • Revisar los valores del atributo OPERACION
    public boolean operacion(OrdenDTO orden) {
        if (!"COMPRA".equals(orden.getOperacion()) && !"VENTA".equals(orden.getOperacion())) {
            log.debug("La operación " + orden.getId() + " de la orden no es válida: " + orden.getOperacion());
            orden.setEstado(1);
            orden.setDescripcion("OPERACION NO VALIDA");
            return false;
        }
        return true;
    }
}

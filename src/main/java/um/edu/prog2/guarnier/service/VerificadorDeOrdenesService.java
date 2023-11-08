package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class VerificadorDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(VerificadorDeOrdenesService.class);

    @Autowired
    CatedraAPIService cs;

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
            orden.setEstado(1);
            orden.setDescripcion("HORA FUERA DE RANGO");
            return false;
        }

        //! 2• Una orden debe tener asociado un cliente y una acción de una compañía. Se debe
        //!    verificar que el Id de cliente y el Id de la acción sean válidos. Para esto
        //!    se debe consultar el servicio cátedra buscando por Id de ambos.
        if (orden.getClienteId() == null || orden.getAccionId() == null) {
            log.debug("La orden " + orden.getId() + " no tiene un cliente o una acción asociada.");
            orden.setEstado(1);
            orden.setDescripcion("SIN CLIENTE O ACCION ASOCIADA");
            return false;
        }

        //! Cliente ID
        String urlCliente = "http://192.168.194.254:8000/api/clientes/buscar";
        JsonNode respuestaCliente = this.cs.getConJWT(urlCliente);
        JsonNode clientes = respuestaCliente.get("clientes");
        boolean clienteValido = false;

        for (JsonNode cliente : clientes) {
            int id = cliente.get("id").asInt();
            if (id == orden.getClienteId()) {
                clienteValido = true;
                orden.setclienteNombre(cliente.get("nombreApellido").asText());
                break;
            }
        }

        if (!clienteValido) {
            log.debug("El cliente asociado a la orden " + orden.getId() + " no es válido: " + orden.getClienteId());
            orden.setEstado(1);
            orden.setDescripcion("CLIENTE NO VALIDO");
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
            orden.setEstado(1);
            orden.setDescripcion("ACCION ID Y ACCION NO VALIDOS");
            return false;
        }

        //! 3• Una orden no puede tener un número de acciones <=0.
        if (orden.getCantidad() <= 0) {
            log.debug("La cantidad de acciones de la orden " + orden.getId() + " es menor o igual a 0.");
            orden.setEstado(1);
            orden.setDescripcion("CANTIDAD DE ACCIONES MENOR O IGUAL A 0");
            return false;
        }

        //! 4• Revisar los valores del atributo MODO
        if (!"AHORA".equals(orden.getModo()) && !"FINDIA".equals(orden.getModo()) && !"PRINCIPIODIA".equals(orden.getModo())) {
            log.debug("El modo de la orden " + orden.getId() + " no es válido: " + orden.getModo());
            orden.setEstado(1);
            orden.setDescripcion("MODO NO VALIDO");
            return false;
        }

        //! 5• Revisar los valores del atributo OPERACION
        if (!"COMPRA".equals(orden.getOperacion()) && !"VENTA".equals(orden.getOperacion())) {
            log.debug("La operación " + orden.getId() + " de la orden no es válida: " + orden.getOperacion());
            orden.setEstado(1);
            orden.setDescripcion("OPERACION NO VALIDA");
            return false;
        }

        //! Si todo está bien, devuelve true.
        return true;
    }
}

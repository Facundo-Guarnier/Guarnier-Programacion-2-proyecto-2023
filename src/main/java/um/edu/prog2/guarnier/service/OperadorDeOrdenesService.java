package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class OperadorDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(OperadorDeOrdenesService.class);

    @Autowired
    OrdenService ordenService;

    @Autowired
    CatedraAPIService catedraAPIService;

    //! Para cuando no puede realizarse la operación.
    public OrdenDTO noEsPosibleOperar(OrdenDTO orden) {
        log.debug("No es posible realizar la operacion" + orden.getId());
        ordenService.update(orden);
        return orden;
    }

    //! Para cuando puede realizarse la operación.
    public OrdenDTO esPosibleOperar(OrdenDTO orden) {
        log.debug("Es posible realizar la operacion " + orden.getId());

        if (orden.getPrecio() == null) {
            orden = cambiarPrecio(orden);
        }

        if (!orden.getModo().equals("AHORA")) {
            programarOrden(orden);
        } else if (orden.getOperacion().equals("COMPRA")) {
            comprarOrden(orden);
        } else if (orden.getOperacion().equals("VENTA")) {
            venderOrden(orden);
        } else {
            return null;
        }

        return orden;
    }

    //! Programar la orden.
    public void programarOrden(OrdenDTO orden) {
        log.info("Programando orden " + orden.getId());
        orden.setEstado(2);
        ordenService.update(orden);
    }

    //! Comprar la orden.
    public boolean venderOrden(OrdenDTO orden) {
        log.info("Vendiendo orden " + orden.getId());
        orden.setEstado(3);
        ordenService.update(orden);
        return true;
    }

    //! Vender la orden.
    public boolean comprarOrden(OrdenDTO orden) {
        log.info("Comprando orden " + orden.getId());
        orden.setEstado(3);
        ordenService.update(orden);
        return true;
    }

    //! Cambia el precio de la orden por el precio actual de la acción.
    public OrdenDTO cambiarPrecio(OrdenDTO orden) {
        String URL = "http://192.168.194.254:8000/api/acciones/ultimovalor/" + orden.getAccion();
        try {
            JsonNode precioJson = catedraAPIService.getConJWT(URL);
            Double precio;
            precio = precioJson.get("ultimoValor").get("valor").asDouble();
            orden.setPrecio(precio.floatValue());
            return orden;
        } catch (Exception e) {
            log.error("No se pudo cambiar el precio de la accion.");
            return orden;
        }
    }
}

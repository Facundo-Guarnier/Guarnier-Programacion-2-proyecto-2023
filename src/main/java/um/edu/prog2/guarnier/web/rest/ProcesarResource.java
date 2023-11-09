package um.edu.prog2.guarnier.web.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.edu.prog2.guarnier.service.ProcesamientoDeOrdenesService;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@RestController
@RequestMapping("/miapi")
public class ProcesarResource {

    private final Logger log = LoggerFactory.getLogger(ProcesarResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    ProcesamientoDeOrdenesService pos;

    //! Endpoint para procesar las ordenes nuevas.
    @GetMapping("/procesar/nuevas/{modo}")
    public String procesarOrdenes(@PathVariable(value = "modo", required = false) final Integer modo) {
        if (modo == 1) {
            log.debug("REST para procesar las ordenes 'www.mockachino.com'.");
        } else if (modo == 2) {
            log.debug("REST para procesar las ordenes default 'Catedra'.");
        } else if (modo == 3) {
            log.debug("REST para procesar las ordenes espejo 'cátedra'.");
        } else {
            log.debug("Id de modo invalida");
            return "Id no válida";
        }

        pos.cargarOrdenes(modo);
        List<List<OrdenDTO>> listas = pos.procesarOrdenes();
        log.info("Ordenes procesadas: " + listas.get(0).size() + " " + listas.get(0));
        log.info("Ordenes fallidas: " + listas.get(1).size() + " " + listas.get(1));
        return "Ordenes procesadas: " + listas.get(0).size() + "\nOrdenes Fallidas: " + listas.get(1).size() + "\n" + listas.toString();
    }

    //! Endpoint para procesar las ordenes ya existentes.
    @GetMapping("/procesar/todas")
    public String procesarOrdenes() {
        List<List<OrdenDTO>> listas = pos.procesarOrdenes();

        log.info("Ordenes procesadas: " + listas.get(0).size() + " " + listas.get(0));
        log.info("Ordenes fallidas: " + listas.get(1).size() + " " + listas.get(1));
        return "Ordenes procesadas: " + listas.get(0).size() + "\nOrdenes Fallidas: " + listas.get(1).size() + "\n" + listas.toString();
    }
}

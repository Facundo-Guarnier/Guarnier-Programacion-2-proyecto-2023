package um.edu.prog2.guarnier.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.edu.prog2.guarnier.service.ProcesamientoDeOrdenesService;

@RestController
@RequestMapping("/miapi")
public class CargarResource {

    private final Logger log = LoggerFactory.getLogger(CargarResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    ProcesamientoDeOrdenesService pos;

    @GetMapping("/cargar/nuevas/{modo}")
    public String procesarOrdenes(@PathVariable(value = "modo", required = false) final Integer modo) {
        if (modo == 1) {
            log.debug("REST para procesar las ordenes 'www.mockachino.com'");
        } else if (modo == 2) {
            log.debug("REST para procesar las ordenes default 'Catedra'");
        } else if (modo == 3) {
            log.debug("REST para procesar las ordenes espejo 'Catedra'");
        } else {
            log.debug("Id de modo invalida");
            return "Id de modo invalida";
        }

        pos.cargarOrdenes(modo);
        return "Ordenes cargadas";
    }
}

package um.edu.prog2.guarnier.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;
import um.edu.prog2.guarnier.service.ServicioExternoService;

@RestController
@RequestMapping("/miapi")
public class CargarResource {

    private final Logger log = LoggerFactory.getLogger(CargarResource.class);

    private static final String ENTITY_NAME = "orden";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    ServicioExternoService servicioExternoService;

    @PostMapping("/cargar/nuevas/{modo}")
    public ResponseEntity<Void> procesarOrdenes(@PathVariable(value = "modo", required = false) final Integer modo) {
        if (modo == 1) {
            log.debug("REST para procesar las ordenes 'www.mockachino.com'");
        } else if (modo == 2) {
            log.debug("REST para procesar las ordenes 'Catedra'");
        } else {
            log.debug("Id de modo invalida");
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(applicationName, true, ENTITY_NAME, "id-invalid", "Id de modo invalida"))
                .build();
        }
        servicioExternoService.cargarOrdenes(modo);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, "procesar"))
            .build();
    }
}

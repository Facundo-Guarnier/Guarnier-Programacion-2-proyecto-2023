package um.edu.prog2.guarnier.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import um.edu.prog2.guarnier.repository.OrdenRepository;
import um.edu.prog2.guarnier.service.AnalizadorDeOrdenesService;
import um.edu.prog2.guarnier.service.OrdenService;
import um.edu.prog2.guarnier.service.ServicioExternoService;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;
import um.edu.prog2.guarnier.web.rest.errors.BadRequestAlertException;

@RestController
@RequestMapping("/api")
public class ReporteResource {

    private final Logger log = LoggerFactory.getLogger(ReporteResource.class);

    private static final String ENTITY_NAME = "orden";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrdenService ordenService;
    private final OrdenRepository ordenRepository;

    @Autowired
    ServicioExternoService servicioExternoService;

    @Autowired
    AnalizadorDeOrdenesService analizadorDeOrdenesService;

    public ReporteResource(OrdenService ordenService, OrdenRepository ordenRepository) {
        this.ordenService = ordenService;
        this.ordenRepository = ordenRepository;
    }

    //! Endpoint para ver el reporte de ordenes segun los filtros aplicados.
    @GetMapping("/reporte")
    public ResponseEntity<List<OrdenDTO>> getReporte(
        @RequestParam(name = "clienteId", required = false) Long clienteId,
        @RequestParam(name = "accionId", required = false) Long accionId,
        @RequestParam(name = "fechaInicio", required = false) String fechaInicioStr,
        @RequestParam(name = "fechaFin", required = false) String fechaFinStr
    ) {
        List<OrdenDTO> ordenes = ordenService.getReporte(clienteId, accionId, fechaInicioStr, fechaFinStr);

        return ResponseEntity.ok().body(ordenes);
    }
}

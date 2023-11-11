package um.edu.prog2.guarnier.web.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import um.edu.prog2.guarnier.domain.Orden;
import um.edu.prog2.guarnier.service.OrdenService;
import um.edu.prog2.guarnier.service.ProcesamientoDeOrdenesService;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@RestController
@RequestMapping("/miapi")
public class ReporteResource {

    private final Logger log = LoggerFactory.getLogger(ReporteResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    ProcesamientoDeOrdenesService pos;

    @Autowired
    OrdenService ordenService;

    //! Endpoint para ver el reporte de ordenes según los filtros aplicados.
    @GetMapping("/reporte")
    public List<OrdenDTO> getReporte(
        @RequestParam(name = "clienteId", required = false) Integer clienteId,
        @RequestParam(name = "accionId", required = false) Integer accionId,
        @RequestParam(name = "fechaInicio", required = false) String fechaInicioStr,
        @RequestParam(name = "fechaFin", required = false) String fechaFinStr
    ) {
        log.debug("REST para ver el reporte de ordenes con filtros.");
        List<OrdenDTO> ordenes = ordenService.getReporte(clienteId, accionId, fechaInicioStr, fechaFinStr);

        return ordenes;
    }
}

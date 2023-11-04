package um.edu.prog2.guarnier.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class ReportarOperacionesService {

    private final Logger log = LoggerFactory.getLogger(ReportarOperacionesService.class);

    public void reportarOperaciones(List<OrdenDTO> ordenesProcesadas2, List<OrdenDTO> ordenesFallidas2) {
        log.debug("Reportando operaciones");
        System.out.println("\n----- Reportando operaciones -----");

        System.out.println("\n----- Ordenes procesadas -----");
        ordenesProcesadas2.forEach(orden -> {
            System.out.println(orden);
        });

        System.out.println("\n----- Ordenes fallidas -----");
        ordenesFallidas2.forEach(orden -> {
            System.out.println(orden);
        });
    }
}

package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.domain.Orden;
import um.edu.prog2.guarnier.service.dto.ListaOrdenesDTO;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class ServicioExternoService {

    private final Logger log = LoggerFactory.getLogger(ServicioExternoService.class);

    @Autowired
    CatedraAPIService cs;

    @Autowired
    ProcesamientoDeOrdenesService procesamientoDeOrdenesService;

    public void simularOrdenes2() {
        System.out.println("\n----- Simulando ordenes -----");

        cs.get("https://www.mockachino.com/2e3476f6-949b-42/api/ordenes/ordenes");

        try {
            List<List<OrdenDTO>> listas = procesamientoDeOrdenesService.analizarOrdenes2();
            System.out.println("Ordenes procesadas: " + listas.get(0).size() + " " + listas.get(0));
            System.out.println("Ordenes fallidas: " + listas.get(1).size() + " " + listas.get(1));
        } catch (Exception e) {
            log.error("Error al analizar las ordenes", e);
        }
    }
}

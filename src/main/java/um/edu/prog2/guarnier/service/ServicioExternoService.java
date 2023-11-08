package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class ServicioExternoService {

    private final Logger log = LoggerFactory.getLogger(ServicioExternoService.class);

    @Autowired
    CatedraAPIService cs;

    @Autowired
    AnalizadorDeOrdenesService analizadorDeOrdenesService;

    @Autowired
    OrdenService ordenService;

    public void simularOrdenes(Integer modo) {
        cargarOrdenes(modo);

        try {
            List<List<OrdenDTO>> listas = analizadorDeOrdenesService.analizarOrdenes();
            // System.out.println("\n\nOrdenes procesadas: " + listas.get(0).size() + " " + listas.get(0));
            // System.out.println("\n\nOrdenes fallidas: " + listas.get(1).size() + " " + listas.get(1));
        } catch (Exception e) {
            log.error("Error al analizar las ordenes", e);
        }
    }

    public void cargarOrdenes(Integer modo) {
        if (modo == 1) {
            log.debug("Simulando ordenes 'www.mockachino.com'");
            JsonNode response = cs.get("https://www.mockachino.com/2e3476f6-949b-42/api/ordenes/ordenes");
            ordenService.guardarNuevas(response);
        } else if (modo == 2) {
            log.debug("Simulando ordenes 'Catedra'");
            JsonNode response = cs.getConJWT("http://192.168.194.254:8000/api/ordenes/ordenes");
            ordenService.guardarNuevas(response);
        } else {
            log.debug("Simular con ordenes existentes en la DB.");
        }
    }
}

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

@Service
@Transactional
public class ServicioExternoService {

    private final Logger log = LoggerFactory.getLogger(ServicioExternoService.class);

    @Autowired
    CatedraAPIService cs;

    @Autowired
    ProcesamientoDeOrdenesService procesamientoDeOrdenesService;

    public void simularOrdenes() {
        //     log.debug("Simulando ordenes");
        //     ProcesamientoDeOrdenesService procesamientoDeOrdenesService = new ProcesamientoDeOrdenesService();
        //     CatedraAPIService cs = new CatedraAPIService();

        //     // JsonNode respuestaCliente = cs.get("https://www.mockachino.com/2e3476f6-949b-42/api/ordenes/ordenes");
        //     JsonNode respuestaCliente = cs.getConJWT("http://192.168.194.254:8000/api/ordenes/ordenes");

        //     try {
        //         ObjectMapper objectMapper = new ObjectMapper();
        //         ListaOrdenesDTO response = objectMapper.readValue(respuestaCliente.toString(), ListaOrdenesDTO.class);
        //         List<Orden> ordenes = response.getOrdenes();

        //         List<List<Orden>> listas = procesamientoDeOrdenesService.analizarOrdenes(ordenes);
        //         System.out.println("Ordenes procesadas: " + listas.get(0).size() + " " + listas.get(0));
        //         System.out.println("Ordenes fallidas: " + listas.get(1).size() + " " + listas.get(1));
        //     } catch (Exception e) {
        //         log.error("Error al analizar las ordenes", e);
        //     }
    }

    public void simularOrdenes2() {
        log.debug("Simulando ordenes");

        cs.get("https://www.mockachino.com/2e3476f6-949b-42/api/ordenes/ordenes");

        try {
            List<List<Orden>> listas = procesamientoDeOrdenesService.analizarOrdenes2();
            System.out.println("Ordenes procesadas: " + listas.get(0).size() + " " + listas.get(0));
            System.out.println("Ordenes fallidas: " + listas.get(1).size() + " " + listas.get(1));
        } catch (Exception e) {
            log.error("Error al analizar las ordenes", e);
        }
    }
}

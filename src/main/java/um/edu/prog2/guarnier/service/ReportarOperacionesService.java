package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class ReportarOperacionesService {

    @Autowired
    CatedraAPIService catedraAPIService;

    private final Logger log = LoggerFactory.getLogger(ReportarOperacionesService.class);

    //! Hace un POST a la cátedra con las ordenes que se procesaron.
    public void reportarOperaciones(List<OrdenDTO> ordenesProcesadas) {
        StringBuilder logMessage = new StringBuilder("IDs de órdenes a reportar: ");

        ObjectNode jsonReporte = JsonNodeFactory.instance.objectNode();
        ArrayNode ordenes = JsonNodeFactory.instance.arrayNode();

        ordenesProcesadas.forEach(orden -> {
            ordenes.add(orden.toReportJson());
            logMessage.append(orden.getId()).append(", ");
        });

        jsonReporte.set("ordenes", ordenes);
        JsonNode jsonNode = jsonReporte;

        log.debug(logMessage.toString());
        //TODO Descomentar para reportar a la cátedra.
        // catedraAPIService.postRoprtar(jsonNode);
    }
}

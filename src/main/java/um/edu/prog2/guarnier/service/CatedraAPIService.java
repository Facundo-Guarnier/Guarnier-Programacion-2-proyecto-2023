package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CatedraAPIService {

    private final Logger log = LoggerFactory.getLogger(CatedraAPIService.class);
    private static final String REPORTE_URL = "http://192.168.194.254:8000/api/reporte-operaciones/reportar";
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzUxMiJ9..."; // Coloca aquí tu token JWT

    @Autowired
    OrdenService ordenService;

    ObjectMapper objectMapper = new ObjectMapper();

    //! Recibe una URL, hace una solicitud HTTP GET, y guarda TODAS las ordenes en la DB.
    public JsonNode get(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            JsonNode responseJsonNode = objectMapper.readTree(connection.getInputStream());

            return responseJsonNode;
        } catch (Exception e) {
            log.error("Error al hacer la get HTTP sin JWT.", e);
            return null;
        }
    }

    //! Recibe una URL, hace una solicitud HTTP GET, y guarda TODAS las ordenes en la DB.
    public JsonNode getConJWT(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJsonNode = objectMapper.readTree(connection.getInputStream());

            return responseJsonNode;
        } catch (Exception e) {
            log.error("Error en la get HTTP con JWT.", e);
            return null;
        }
    }

    public void postConJWT(JsonNode ordenes) {
        try {
            URL url = new URL(REPORTE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            //! Convierte el JsonNode en una cadena JSON
            String ordenesJSON = new ObjectMapper().writeValueAsString(ordenes);

            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.write(ordenesJSON.getBytes("UTF-8"));
            }

            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            connection.getInputStream();
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el informe de operaciones a JSON.", e);
        } catch (Exception e) {
            log.error("Error en la post HTTP con JWT.", e);
        }
    }
}
//! Ejemplo de JSON que recibe, solo los 2 primeros tienen que fallar
//! https://www.mockachino.com/spaces/2e3476f6-949b-42
// {
//   "ordenes": [
//     {
//       "cliente": 26364,
//       "accionId": 1,
//       "accion": "AAPL",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 10,
//       "fechaOperacion": "2023-09-25T03:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 3,
//       "accion": "INTC",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 0,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 2,
//       "accion": "GOOGL",
//       "operacion": "VENTA",
//       "precio": null,
//       "cantidad": 5,
//       "fechaOperacion": "2023-09-25T03:00:00Z",
//       "modo": "FINDIA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 4,
//       "accion": "KO",
//       "operacion": "COMPRA",
//       "precio": null,
//       "cantidad": 80,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     },
//     {
//       "cliente": 26364,
//       "accionId": 6,
//       "accion": "YPF",
//       "operacion": "VENTA",
//       "precio": null,
//       "cantidad": 5,
//       "fechaOperacion": "2023-09-25T13:00:00Z",
//       "modo": "AHORA"
//     }
//   ]
// }

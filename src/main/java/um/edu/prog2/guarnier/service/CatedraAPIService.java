package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class CatedraAPIService {

    private final Logger log = LoggerFactory.getLogger(CatedraAPIService.class);
    private static final String REPORTE_URL = "http://192.168.194.254:8000/api/reporte-operaciones/reportar";
    private static final String ESPEJO_URL = "http://192.168.194.254:8000/api/ordenes/espejo";
    private static final String JWT_TOKEN =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYWN1bmRvZ3Vhcm5pZXIiLCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNzI5NzUzNzcyfQ.pklknWchQH_Y8kM8Is-XCfu6hYxWVJJqgg0rNBAH9IisOWKPW1n-jC3Xqecv6HFjwHvWc3nugiaB5gtMaNlShg";

    @Autowired
    OrdenService ordenService;

    @Autowired
    ResourceLoader resourceLoader;

    ObjectMapper objectMapper = new ObjectMapper();

    //! Recibe una URL, hace una solicitud HTTP GET, y guarda TODAS las ordenes en la DB.
    public JsonNode get(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            ObjectMapper objectMapper = new ObjectMapper();

            //! Configuración para que el ObjectMapper convierta las fechas a UTC
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));

            JsonNode responseJsonNode = objectMapper.readTree(connection.getInputStream());

            return responseJsonNode;
        } catch (Exception e) {
            log.error("Error al hacer la get HTTP sin JWT.");
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
            log.error("Error en la get HTTP con JWT.");
            return null;
        }
    }

    //! Recibe las ordenes a reportar a la API de reporte-operaciones de la catedra
    public void postRoprtar(JsonNode ordenes) {
        try {
            URL url = new URL(REPORTE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String ordenesJSON = new ObjectMapper().writeValueAsString(ordenes);

            //! Envía la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = ordenesJSON.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            //! Recibe la respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.debug("Reporte exitoso.");
            } else {
                log.error("Error en la solicitud HTTP. Código de respuesta: " + responseCode);
            }
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el informe de operaciones a JSON.");
        } catch (Exception e) {
            log.error("Error en la solicitud HTTP con JWT.");
        }
    }

    //! Hace un POST con 2 ordenes aleatorias a la api de espejo.
    public void postEspejo() {
        try {
            URL url = new URL(ESPEJO_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + JWT_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String ordenesJSON = new ObjectMapper().writeValueAsString(ordenesAleatorias());

            //! Envía la solicitud
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = ordenesJSON.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }

            //! Recibe la respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                log.debug("Reporte exitoso.");
            } else {
                log.error("Error en la solicitud HTTP. Código de respuesta: " + responseCode);
            }
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el informe de operaciones a JSON.");
        } catch (Exception e) {
            log.error("Error en la solicitud HTTP con JWT.");
        }
    }

    //! Devuelve un JsonNode con 2 ordenes aleatorias provenientes del archivo "src\main\resources\static\ordenesEspejo.json".
    private JsonNode ordenesAleatorias() {
        String rutaArchivo = "classpath:static/ordenesEspejo.json";
        String json = leerContenidoArchivo(rutaArchivo);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<OrdenDTO> ordenes = objectMapper.readValue(json, new TypeReference<List<OrdenDTO>>() {});
            List<OrdenDTO> ordenesSeleccionadas = new ArrayList<>();

            Random random = new Random();
            while (ordenesSeleccionadas.size() < 2) {
                int indiceAleatorio = random.nextInt(ordenes.size());
                OrdenDTO ordenAleatoria = ordenes.get(indiceAleatorio);
                ordenesSeleccionadas.add(ordenAleatoria);
            }

            Map<String, List<OrdenDTO>> contenedor = new HashMap<>();
            contenedor.put("ordenes", ordenesSeleccionadas);

            JsonNode jsonNode = objectMapper.valueToTree(contenedor);
            return jsonNode;
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el informe de operaciones a JSON.");
            return null;
        }
    }

    //! Leer el contenido del .json con ordenes.
    private String leerContenidoArchivo(String rutaArchivo) {
        try {
            Resource resource = resourceLoader.getResource(rutaArchivo);
            InputStream inputStream = resource.getInputStream();
            byte[] contenido = StreamUtils.copyToByteArray(inputStream);
            return new String(contenido, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error al leer el contenido del archivo.");
            return null;
        }
    }
}

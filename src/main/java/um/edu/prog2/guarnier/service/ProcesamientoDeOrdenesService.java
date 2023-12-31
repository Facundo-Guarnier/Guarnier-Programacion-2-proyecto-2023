package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.exception.FalloConexionCatedraException;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class ProcesamientoDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private List<OrdenDTO> ordenesProcesadas = new ArrayList<OrdenDTO>();
    private List<OrdenDTO> ordenesFallidas = new ArrayList<OrdenDTO>();
    private List<OrdenDTO> ordenesFinalizadas = new ArrayList<OrdenDTO>();

    @Value("${constantes.mockachino-url}")
    public String MOCKACHINO_URL;

    @Value("${constantes.catedra-url}")
    public String CATEDRA_URL;

    @Value("${constantes.espejo-get-url}")
    public String ESPEJO_GET_URL;

    @Autowired
    VerificadorDeOrdenesService vos;

    @Autowired
    OrdenService ordenService;

    @Autowired
    ReportarOperacionesService ros;

    @Autowired
    OperadorDeOrdenesService oos;

    @Autowired
    CatedraAPIService cs;

    @PostConstruct
    public void init() {
        log.info("Iniciando 'AnalizadorDeOrdenesService'.");

        //! Funcion, retraso inicial, intervalo de ejecución (1440 minutos = 24 horas), unidad de tiempo
        scheduler.scheduleAtFixedRate(
            () -> {
                cargarOrdenes(3);
                List<List<OrdenDTO>> r = procesarOrdenes();
            },
            10000,
            10,
            TimeUnit.SECONDS
        );
    }

    //! Método que tiene que leer la DB, analizar las ordenes y devolver 2 listas con las procesadas y las fallidas.
    public List<List<OrdenDTO>> procesarOrdenes() {
        this.ordenesProcesadas.clear();
        this.ordenesFallidas.clear();
        this.ordenesFinalizadas.clear();

        try {
            ordenService
                .findPendientes()
                .forEach(orden -> {
                    log.debug("Procesando ordenes instantáneas: " + orden);
                    try {
                        if (vos.puedeRealizarOperacion(orden)) {
                            this.ordenesProcesadas.add(oos.esPosibleOperar(orden));
                        } else {
                            this.ordenesFallidas.add(oos.noEsPosibleOperar(orden));
                        }
                    } catch (FalloConexionCatedraException e) {
                        log.error("Error al procesar", e.getMessage());
                    }
                });
        } catch (Exception e) {
            log.error("Error al buscar ordenes en DB y analizarlas.");
        }

        //! Devuelve una lista de listas, la primera con las ordenes procesadas y la segunda con las fallidas
        List<List<OrdenDTO>> resultado = new ArrayList<>();
        resultado.add(ordenesProcesadas);
        resultado.add(ordenesFallidas);

        log.debug("Ordenes procesadas: " + resultado.get(0).size() + " " + resultado.get(0));
        log.debug("Ordenes fallidas: " + resultado.get(1).size() + " " + resultado.get(1));

        //! ORdenes finalizadas
        for (OrdenDTO orden : ordenesProcesadas) {
            if ("AHORA".equals(orden.getModo())) {
                ordenesFinalizadas.add(orden);
            }
        }
        if (ordenesFinalizadas.size() > 0) {
            ros.reportarOperaciones(ordenesFinalizadas);
        }
        return resultado;
    }

    //! Método que guarda en DB las ordenes
    public void cargarOrdenes(Integer modo) {
        if (modo == 1) {
            log.debug("Cargando ordenes 'www.mockachino.com'");
            JsonNode response = cs.get(MOCKACHINO_URL);
            ordenService.guardarNuevas(response);
        } else if (modo == 2) {
            log.debug("Cargando ordenes 'Catedra'");
            JsonNode response = cs.getConJWT(CATEDRA_URL);
            ordenService.guardarNuevas(response);
        } else if (modo == 3) {
            log.debug("Cargando ordenes espejo 'Catedra'");
            cs.postEspejo();
            JsonNode response = cs.getConJWT(ESPEJO_GET_URL);
            ordenService.guardarNuevas(response);
        }
    }
}

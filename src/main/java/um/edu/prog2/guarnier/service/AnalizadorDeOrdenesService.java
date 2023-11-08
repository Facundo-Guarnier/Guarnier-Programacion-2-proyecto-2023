package um.edu.prog2.guarnier.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@Service
@Transactional
public class AnalizadorDeOrdenesService {

    private final Logger log = LoggerFactory.getLogger(AnalizadorDeOrdenesService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    public List<OrdenDTO> ordenesProcesadas = new ArrayList<OrdenDTO>();
    public List<OrdenDTO> ordenesFallidas = new ArrayList<OrdenDTO>();

    @Autowired
    VerificadorDeOrdenesService vos;

    @Autowired
    OrdenService ordenService;

    @Autowired
    ReportarOperacionesService ros;

    @Autowired
    OperadorDeOrdenesService oos;

    @PostConstruct
    public void init() {
        log.info("Iniciando 'AnalizadorDeOrdenesService'");

        //! Funcion, retraso inicial, intervalo de ejecución (1440 minutos = 24 horas), unidad de tiempo
        scheduler.scheduleAtFixedRate(
            () -> {
                List<List<OrdenDTO>> r = analizarOrdenes();
            },
            10000,
            10,
            TimeUnit.SECONDS
        );
    }

    //! Método que tiene que leer la DB, analizar las ordenes y devolver 2 listas con las procesadas y las fallidas.
    public List<List<OrdenDTO>> analizarOrdenes() {
        this.ordenesProcesadas.clear();
        this.ordenesFallidas.clear();

        log.debug("Analizando ordenes");
        try {
            ordenService
                .findPendientes()
                .forEach(orden -> {
                    log.debug("Procesando ordenes instantáneas: " + orden);
                    if (vos.puedeRealizarOperacion(orden)) {
                        this.ordenesFallidas.add(oos.esPosibleOperar(orden));
                    } else {
                        this.ordenesFallidas.add(oos.noEsPosibleOperar(orden));
                    }
                });
        } catch (Exception e) {
            log.error("Error al buscar ordenes en DB y analizarlas.", e);
        }

        //! Devuelve una lista de listas, la primera con las ordenes procesadas y la segunda con las fallidas
        List<List<OrdenDTO>> resultado = new ArrayList<>();
        resultado.add(ordenesProcesadas);
        resultado.add(ordenesFallidas);

        ros.reportarOperaciones(ordenesProcesadas, ordenesFallidas);

        return resultado;
    }
}

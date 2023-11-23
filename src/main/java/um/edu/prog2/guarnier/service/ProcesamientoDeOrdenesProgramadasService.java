package um.edu.prog2.guarnier.service;

import java.time.Duration;
import java.time.LocalDateTime;
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
public class ProcesamientoDeOrdenesProgramadasService {

    private final Logger log = LoggerFactory.getLogger(ProcesamientoDeOrdenesProgramadasService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @Autowired
    CatedraAPIService catedraAPIService;

    @Autowired
    OperadorDeOrdenesService oos;

    @Autowired
    OrdenService ordenService;

    //! Programar ejecución a las 9 AM y a las 6 PM.
    //* Spring utiliza un programador de tareas en segundo plano (background task scheduler) para administrar las tareas programadas.
    @PostConstruct
    public void init() {
        log.info("Iniciando 'ProcesamientoDeOrdenesProgramadasService'");

        //* Funcion, retraso inicial, intervalo de ejecución (1440 minutos = 24 horas), unidad de tiempo
        scheduler.scheduleAtFixedRate(() -> procesar(9), calcularRetrasoHastaProximaEjecucion(9, 0), 1440, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(() -> procesar(18), calcularRetrasoHastaProximaEjecucion(18, 0), 1440, TimeUnit.MINUTES);
    }

    //! Método que tiene que leer la DB y analizar las ordenes
    private void procesar(Integer hora) {
        log.info("Ejecutando el ordenes programadas a las" + hora + ":00");

        ordenService
            .findProgramados()
            .forEach(orden -> {
                log.debug("Procesando ordenes programada: " + orden);

                if (hora == this.horaOrden(orden)) {
                    orden = oos.cambiarPrecio(orden);
                    if (orden.getOperacion().equals("COMPRA")) {
                        oos.comprarOrden(orden);
                    } else if (orden.getOperacion().equals("VENTA")) {
                        oos.venderOrden(orden);
                    }
                }
            });
    }

    //! Devuelve la hora a la que se debe ejecutar la orden.
    private Integer horaOrden(OrdenDTO orden) {
        if ("FINDIA".equals(orden.getModo())) {
            return 18;
        } else {
            return 9;
        }
    }

    //! Calcula el retraso hasta la próxima ejecución.
    private long calcularRetrasoHastaProximaEjecucion(int hora, int minuto) {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime horaDeseada = LocalDateTime.of(ahora.getYear(), ahora.getMonth(), ahora.getDayOfMonth(), hora, minuto);

        //! Si la hora ya pasó hoy, programa la ejecución para mañana.
        if (horaDeseada.isBefore(ahora)) {
            horaDeseada = horaDeseada.plusDays(1);
        }

        Duration retraso = Duration.between(ahora, horaDeseada);
        return retraso.toMinutes();
    }
}

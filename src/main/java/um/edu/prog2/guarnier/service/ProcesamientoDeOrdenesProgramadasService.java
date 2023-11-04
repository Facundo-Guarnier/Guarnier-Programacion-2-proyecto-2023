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
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    ProcesamientoDeOrdenesService procesamientoDeOrdenesService;

    @Autowired
    OrdenService ordenService;

    //! Programar ejecución a las 9 AM y a las 6 PM.
    //! Spring utiliza un programador de tareas en segundo plano (background task scheduler) para administrar las tareas programadas.
    @PostConstruct
    public void init() {
        System.out.println("\n\n\n\n----- Iniciando ProcesamientoDeOrdenesProgramadasService -----\n\n\n\n");

        scheduler.scheduleAtFixedRate(this::procesar, calcularRetrasoHastaProximaEjecucion(14, 39), 24, TimeUnit.HOURS);
        // scheduler.scheduleAtFixedRate(this::procesar, calcularRetrasoHastaProximaEjecucion(18, 0), 12, TimeUnit.HOURS);

        //! Funcion, retraso inicial en milisegundos, intervalo de ejecución, unidad de tiempo
        // scheduler.scheduleAtFixedRate(() -> procesar(9), calcularRetrasoHastaProximaEjecucion(14, 27), 24, TimeUnit.HOURS);
        // scheduler.scheduleAtFixedRate(() -> procesar(18), calcularRetrasoHastaProximaEjecucion(18, 0), 24, TimeUnit.HOURS);

    }

    //! Método que tiene que leer la DB y analizar las ordenes
    // public void procesar(Integer hora) {
    public void procesar() {
        Integer hora = 9;
        log.info("\n\n\n\n\n------- Ejecutando el ordenes programadas a las 9 AM. -------");
        log.info("\n\n\n\n\n------- Ejecutando el ordenes programadas a las 9 AM. -------");
        System.out.println("\n------- Ejecutando el ordenes programadas a las 9 AM. -------");

        ordenService
            .findProgramadas()
            .forEach(orden -> {
                System.out.println("\n----- Procesamiento -----\n" + orden);

                if (hora == this.horaOrden(orden)) {
                    if (orden.getOperacion().equals("COMPRA")) {
                        procesamientoDeOrdenesService.comprarOrden(orden);
                    } else if (orden.getOperacion().equals("VENTA")) {
                        procesamientoDeOrdenesService.venderOrden(orden);
                    }
                }
            });
    }

    //! Devuelve la hora a la que se debe ejecutar la orden.
    public Integer horaOrden(OrdenDTO orden) {
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
        System.out.println("\nDuración: " + retraso.toMillis() + " milisegundos\n\n");
        return retraso.toMillis();
    }
}

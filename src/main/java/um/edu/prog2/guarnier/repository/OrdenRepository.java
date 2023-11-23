package um.edu.prog2.guarnier.repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.edu.prog2.guarnier.domain.Orden;

/**
 * Spring Data JPA repository for the Orden entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    //! Metodo para buscar una orden en base a su estado
    List<Orden> findByEstado(Integer estado);

    @Query(
        "SELECT o FROM Orden o " +
        "WHERE (:clienteId IS NULL OR o.cliente = :clienteId) " +
        "AND (:accionId IS NULL OR o.accionId = :accionId) " +
        "AND (:fechaInicio IS NULL OR o.fechaOperacion >= :fechaInicio) " +
        "AND (:fechaFin IS NULL OR o.fechaOperacion <= :fechaFin)"
    )
    List<Orden> buscarReportes(
        @Param("clienteId") Integer clienteId,
        @Param("accionId") Integer accionId,
        @Param("fechaInicio") Instant fechaInicio,
        @Param("fechaFin") Instant fechaFin
    );
}

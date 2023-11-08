package um.edu.prog2.guarnier.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
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
}

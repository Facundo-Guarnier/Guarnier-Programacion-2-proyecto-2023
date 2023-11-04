package um.edu.prog2.guarnier.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import um.edu.prog2.guarnier.domain.Orden;

/**
 * Spring Data JPA repository for the Orden entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {}

package um.edu.prog2.guarnier.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.edu.prog2.guarnier.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}

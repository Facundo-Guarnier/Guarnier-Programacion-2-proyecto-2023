package um.edu.prog2.guarnier.service.mapper;

import org.mapstruct.*;
import um.edu.prog2.guarnier.domain.Orden;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

/**
 * Mapper for the entity {@link Orden} and its DTO {@link OrdenDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrdenMapper extends EntityMapper<OrdenDTO, Orden> {}

package um.edu.prog2.guarnier.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.prog2.guarnier.domain.Orden;
import um.edu.prog2.guarnier.repository.OrdenRepository;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;
import um.edu.prog2.guarnier.service.mapper.OrdenMapper;

@Service
@Transactional
public class OrdenService {

    private final Logger log = LoggerFactory.getLogger(OrdenService.class);

    private final OrdenRepository ordenRepository;

    private final OrdenMapper ordenMapper;

    public OrdenService(OrdenRepository ordenRepository, OrdenMapper ordenMapper) {
        this.ordenRepository = ordenRepository;
        this.ordenMapper = ordenMapper;
    }

    //! -----------------------------------------------------
    //! Metodos creados por mi

    //! Agregado para guardar solo instancias de orden.
    // public OrdenDTO saveOrden(Orden orden) {
    //     log.debug("Request to save Orden : {}", orden);
    //     orden = ordenRepository.save(orden);
    //     return ordenMapper.toDto(orden);
    // }

    //! Metodo para buscar una orden en base a su estado PENDIENTE
    /**
     * Buscar todas las órdenes con estado "PENDIENTE".
     *
     * @return Lista de órdenes con estado "PENDIENTE".
     */
    @Transactional(readOnly = true)
    public List<OrdenDTO> findPendientes() {
        log.debug("Request to get all Ordenes with estado PENDIENTE");
        return ordenRepository.findByEstado("PENDIENTE").stream().map(ordenMapper::toDto).collect(Collectors.toList());
    }

    //! -----------------------------------------------------
    //! Metodos creados por jhipster
    /**
     * Save a orden.
     *
     * @param ordenDTO the entity to save.
     * @return the persisted entity.
     */
    public OrdenDTO save(OrdenDTO ordenDTO) {
        log.debug("Request to save Orden : {}", ordenDTO);
        Orden orden = ordenMapper.toEntity(ordenDTO);
        orden = ordenRepository.save(orden);
        return ordenMapper.toDto(orden);
    }

    /**
     * Update a orden.
     *
     * @param ordenDTO the entity to save.
     * @return the persisted entity.
     */
    public OrdenDTO update(OrdenDTO ordenDTO) {
        log.debug("Request to update Orden : {}", ordenDTO);
        Orden orden = ordenMapper.toEntity(ordenDTO);
        orden = ordenRepository.save(orden);
        return ordenMapper.toDto(orden);
    }

    /**
     * Partially update a orden.
     *
     * @param ordenDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OrdenDTO> partialUpdate(OrdenDTO ordenDTO) {
        log.debug("Request to partially update Orden : {}", ordenDTO);

        return ordenRepository
            .findById(ordenDTO.getId())
            .map(existingOrden -> {
                ordenMapper.partialUpdate(existingOrden, ordenDTO);

                return existingOrden;
            })
            .map(ordenRepository::save)
            .map(ordenMapper::toDto);
    }

    /**
     * Get all the ordens.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OrdenDTO> findAll() {
        log.debug("Request to get all Ordens");
        return ordenRepository.findAll().stream().map(ordenMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one orden by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OrdenDTO> findOne(Long id) {
        log.debug("Request to get Orden : {}", id);
        return ordenRepository.findById(id).map(ordenMapper::toDto);
    }

    /**
     * Delete the orden by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Orden : {}", id);
        ordenRepository.deleteById(id);
    }
}
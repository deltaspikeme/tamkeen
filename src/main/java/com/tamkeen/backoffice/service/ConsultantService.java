package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.Consultant;
import com.tamkeen.backoffice.repository.ConsultantRepository;
import com.tamkeen.backoffice.repository.search.ConsultantSearchRepository;
import com.tamkeen.backoffice.service.dto.ConsultantDTO;
import com.tamkeen.backoffice.service.mapper.ConsultantMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.Consultant}.
 */
@Service
public class ConsultantService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultantService.class);

    private final ConsultantRepository consultantRepository;

    private final ConsultantMapper consultantMapper;

    private final ConsultantSearchRepository consultantSearchRepository;

    public ConsultantService(
        ConsultantRepository consultantRepository,
        ConsultantMapper consultantMapper,
        ConsultantSearchRepository consultantSearchRepository
    ) {
        this.consultantRepository = consultantRepository;
        this.consultantMapper = consultantMapper;
        this.consultantSearchRepository = consultantSearchRepository;
    }

    /**
     * Save a consultant.
     *
     * @param consultantDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsultantDTO save(ConsultantDTO consultantDTO) {
        LOG.debug("Request to save Consultant : {}", consultantDTO);
        Consultant consultant = consultantMapper.toEntity(consultantDTO);
        consultant = consultantRepository.save(consultant);
        consultantSearchRepository.index(consultant);
        return consultantMapper.toDto(consultant);
    }

    /**
     * Update a consultant.
     *
     * @param consultantDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsultantDTO update(ConsultantDTO consultantDTO) {
        LOG.debug("Request to update Consultant : {}", consultantDTO);
        Consultant consultant = consultantMapper.toEntity(consultantDTO);
        consultant = consultantRepository.save(consultant);
        consultantSearchRepository.index(consultant);
        return consultantMapper.toDto(consultant);
    }

    /**
     * Partially update a consultant.
     *
     * @param consultantDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConsultantDTO> partialUpdate(ConsultantDTO consultantDTO) {
        LOG.debug("Request to partially update Consultant : {}", consultantDTO);

        return consultantRepository
            .findById(consultantDTO.getId())
            .map(existingConsultant -> {
                consultantMapper.partialUpdate(existingConsultant, consultantDTO);

                return existingConsultant;
            })
            .map(consultantRepository::save)
            .map(savedConsultant -> {
                consultantSearchRepository.index(savedConsultant);
                return savedConsultant;
            })
            .map(consultantMapper::toDto);
    }

    /**
     * Get all the consultants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<ConsultantDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Consultants");
        return consultantRepository.findAll(pageable).map(consultantMapper::toDto);
    }

    /**
     * Get one consultant by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ConsultantDTO> findOne(String id) {
        LOG.debug("Request to get Consultant : {}", id);
        return consultantRepository.findById(id).map(consultantMapper::toDto);
    }

    /**
     * Delete the consultant by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Consultant : {}", id);
        consultantRepository.deleteById(id);
        consultantSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the consultant corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<ConsultantDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Consultants for query {}", query);
        return consultantSearchRepository.search(query, pageable).map(consultantMapper::toDto);
    }
}

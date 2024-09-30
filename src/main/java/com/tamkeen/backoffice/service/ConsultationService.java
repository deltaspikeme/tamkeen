package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.Consultation;
import com.tamkeen.backoffice.repository.ConsultationRepository;
import com.tamkeen.backoffice.repository.search.ConsultationSearchRepository;
import com.tamkeen.backoffice.service.dto.ConsultationDTO;
import com.tamkeen.backoffice.service.mapper.ConsultationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.Consultation}.
 */
@Service
public class ConsultationService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultationService.class);

    private final ConsultationRepository consultationRepository;

    private final ConsultationMapper consultationMapper;

    private final ConsultationSearchRepository consultationSearchRepository;

    public ConsultationService(
        ConsultationRepository consultationRepository,
        ConsultationMapper consultationMapper,
        ConsultationSearchRepository consultationSearchRepository
    ) {
        this.consultationRepository = consultationRepository;
        this.consultationMapper = consultationMapper;
        this.consultationSearchRepository = consultationSearchRepository;
    }

    /**
     * Save a consultation.
     *
     * @param consultationDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsultationDTO save(ConsultationDTO consultationDTO) {
        LOG.debug("Request to save Consultation : {}", consultationDTO);
        Consultation consultation = consultationMapper.toEntity(consultationDTO);
        consultation = consultationRepository.save(consultation);
        consultationSearchRepository.index(consultation);
        return consultationMapper.toDto(consultation);
    }

    /**
     * Update a consultation.
     *
     * @param consultationDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsultationDTO update(ConsultationDTO consultationDTO) {
        LOG.debug("Request to update Consultation : {}", consultationDTO);
        Consultation consultation = consultationMapper.toEntity(consultationDTO);
        consultation = consultationRepository.save(consultation);
        consultationSearchRepository.index(consultation);
        return consultationMapper.toDto(consultation);
    }

    /**
     * Partially update a consultation.
     *
     * @param consultationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConsultationDTO> partialUpdate(ConsultationDTO consultationDTO) {
        LOG.debug("Request to partially update Consultation : {}", consultationDTO);

        return consultationRepository
            .findById(consultationDTO.getId())
            .map(existingConsultation -> {
                consultationMapper.partialUpdate(existingConsultation, consultationDTO);

                return existingConsultation;
            })
            .map(consultationRepository::save)
            .map(savedConsultation -> {
                consultationSearchRepository.index(savedConsultation);
                return savedConsultation;
            })
            .map(consultationMapper::toDto);
    }

    /**
     * Get all the consultations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<ConsultationDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Consultations");
        return consultationRepository.findAll(pageable).map(consultationMapper::toDto);
    }

    /**
     * Get one consultation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<ConsultationDTO> findOne(String id) {
        LOG.debug("Request to get Consultation : {}", id);
        return consultationRepository.findById(id).map(consultationMapper::toDto);
    }

    /**
     * Delete the consultation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Consultation : {}", id);
        consultationRepository.deleteById(id);
        consultationSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the consultation corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<ConsultationDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Consultations for query {}", query);
        return consultationSearchRepository.search(query, pageable).map(consultationMapper::toDto);
    }
}

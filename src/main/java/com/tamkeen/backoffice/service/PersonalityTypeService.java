package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.PersonalityType;
import com.tamkeen.backoffice.repository.PersonalityTypeRepository;
import com.tamkeen.backoffice.repository.search.PersonalityTypeSearchRepository;
import com.tamkeen.backoffice.service.dto.PersonalityTypeDTO;
import com.tamkeen.backoffice.service.mapper.PersonalityTypeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.PersonalityType}.
 */
@Service
public class PersonalityTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalityTypeService.class);

    private final PersonalityTypeRepository personalityTypeRepository;

    private final PersonalityTypeMapper personalityTypeMapper;

    private final PersonalityTypeSearchRepository personalityTypeSearchRepository;

    public PersonalityTypeService(
        PersonalityTypeRepository personalityTypeRepository,
        PersonalityTypeMapper personalityTypeMapper,
        PersonalityTypeSearchRepository personalityTypeSearchRepository
    ) {
        this.personalityTypeRepository = personalityTypeRepository;
        this.personalityTypeMapper = personalityTypeMapper;
        this.personalityTypeSearchRepository = personalityTypeSearchRepository;
    }

    /**
     * Save a personalityType.
     *
     * @param personalityTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonalityTypeDTO save(PersonalityTypeDTO personalityTypeDTO) {
        LOG.debug("Request to save PersonalityType : {}", personalityTypeDTO);
        PersonalityType personalityType = personalityTypeMapper.toEntity(personalityTypeDTO);
        personalityType = personalityTypeRepository.save(personalityType);
        personalityTypeSearchRepository.index(personalityType);
        return personalityTypeMapper.toDto(personalityType);
    }

    /**
     * Update a personalityType.
     *
     * @param personalityTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonalityTypeDTO update(PersonalityTypeDTO personalityTypeDTO) {
        LOG.debug("Request to update PersonalityType : {}", personalityTypeDTO);
        PersonalityType personalityType = personalityTypeMapper.toEntity(personalityTypeDTO);
        personalityType = personalityTypeRepository.save(personalityType);
        personalityTypeSearchRepository.index(personalityType);
        return personalityTypeMapper.toDto(personalityType);
    }

    /**
     * Partially update a personalityType.
     *
     * @param personalityTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PersonalityTypeDTO> partialUpdate(PersonalityTypeDTO personalityTypeDTO) {
        LOG.debug("Request to partially update PersonalityType : {}", personalityTypeDTO);

        return personalityTypeRepository
            .findById(personalityTypeDTO.getId())
            .map(existingPersonalityType -> {
                personalityTypeMapper.partialUpdate(existingPersonalityType, personalityTypeDTO);

                return existingPersonalityType;
            })
            .map(personalityTypeRepository::save)
            .map(savedPersonalityType -> {
                personalityTypeSearchRepository.index(savedPersonalityType);
                return savedPersonalityType;
            })
            .map(personalityTypeMapper::toDto);
    }

    /**
     * Get all the personalityTypes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<PersonalityTypeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PersonalityTypes");
        return personalityTypeRepository.findAll(pageable).map(personalityTypeMapper::toDto);
    }

    /**
     * Get one personalityType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<PersonalityTypeDTO> findOne(String id) {
        LOG.debug("Request to get PersonalityType : {}", id);
        return personalityTypeRepository.findById(id).map(personalityTypeMapper::toDto);
    }

    /**
     * Delete the personalityType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete PersonalityType : {}", id);
        personalityTypeRepository.deleteById(id);
        personalityTypeSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the personalityType corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<PersonalityTypeDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PersonalityTypes for query {}", query);
        return personalityTypeSearchRepository.search(query, pageable).map(personalityTypeMapper::toDto);
    }
}

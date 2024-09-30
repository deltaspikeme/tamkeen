package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.repository.PersonalityTestRepository;
import com.tamkeen.backoffice.repository.search.PersonalityTestSearchRepository;
import com.tamkeen.backoffice.service.dto.PersonalityTestDTO;
import com.tamkeen.backoffice.service.mapper.PersonalityTestMapper;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.PersonalityTest}.
 */
@Service
public class PersonalityTestService {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalityTestService.class);

    private final PersonalityTestRepository personalityTestRepository;
    private final QuestionService questionService;
    private final PersonalityTestMapper personalityTestMapper;

    private final PersonalityTestSearchRepository personalityTestSearchRepository;

    public PersonalityTestService(
        PersonalityTestRepository personalityTestRepository,
        QuestionService questionService,
        PersonalityTestMapper personalityTestMapper,
        PersonalityTestSearchRepository personalityTestSearchRepository
    ) {
        this.personalityTestRepository = personalityTestRepository;
        this.questionService = questionService;
        this.personalityTestMapper = personalityTestMapper;
        this.personalityTestSearchRepository = personalityTestSearchRepository;
    }

    /**
     * Save a personalityTest.
     *
     * @param personalityTestDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonalityTestDTO save(PersonalityTestDTO personalityTestDTO) {
        LOG.debug("Request to save PersonalityTest : {}", personalityTestDTO);
        PersonalityTest personalityTest = personalityTestMapper.toEntity(personalityTestDTO);
        personalityTest = personalityTestRepository.save(personalityTest);
        personalityTestSearchRepository.index(personalityTest);
        return personalityTestMapper.toDto(personalityTest);
    }

    /**
     * Update a personalityTest.
     *
     * @param personalityTestDTO the entity to save.
     * @return the persisted entity.
     */
    public PersonalityTestDTO update(PersonalityTestDTO personalityTestDTO) {
        LOG.debug("Request to update PersonalityTest : {}", personalityTestDTO);
        PersonalityTest personalityTest = personalityTestMapper.toEntity(personalityTestDTO);
        personalityTest = personalityTestRepository.save(personalityTest);
        personalityTestSearchRepository.index(personalityTest);
        return personalityTestMapper.toDto(personalityTest);
    }

    /**
     * Partially update a personalityTest.
     *
     * @param personalityTestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PersonalityTestDTO> partialUpdate(PersonalityTestDTO personalityTestDTO) {
        LOG.debug("Request to partially update PersonalityTest : {}", personalityTestDTO);

        return personalityTestRepository
            .findById(personalityTestDTO.getId())
            .map(existingPersonalityTest -> {
                personalityTestMapper.partialUpdate(existingPersonalityTest, personalityTestDTO);

                return existingPersonalityTest;
            })
            .map(personalityTestRepository::save)
            .map(savedPersonalityTest -> {
                personalityTestSearchRepository.index(savedPersonalityTest);
                return savedPersonalityTest;
            })
            .map(personalityTestMapper::toDto);
    }

    /**
     * Get all the personalityTests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<PersonalityTestDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PersonalityTests");
        return personalityTestRepository.findAll(pageable).map(personalityTestMapper::toDto);
    }

    /**
     * Get one personalityTest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<PersonalityTestDTO> findOne(String id) {
        LOG.debug("Request to get PersonalityTest : {}", id);
        // Use AtomicReference to hold the result
        AtomicReference<Optional<PersonalityTestDTO>> result = new AtomicReference<>(Optional.empty());

        personalityTestRepository
            .findById(id)
            .map(personalityTestMapper::toDto)
            .ifPresent(test -> {
                questionService
                    .findByPersonalityTest(personalityTestMapper.toEntity(test))
                    .ifPresent(questions -> {
                        test.setQuestions(Set.copyOf(questions));
                    });
                result.set(Optional.of(test)); // Set the result using AtomicReference
            });

        return result.get(); // Return the value from AtomicReference
    }

    /**
     * Delete the personalityTest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete PersonalityTest : {}", id);
        personalityTestRepository.deleteById(id);
        personalityTestSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the personalityTest corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<PersonalityTestDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of PersonalityTests for query {}", query);
        return personalityTestSearchRepository.search(query, pageable).map(personalityTestMapper::toDto);
    }
}

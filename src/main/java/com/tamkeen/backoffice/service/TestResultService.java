package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.TestResult;
import com.tamkeen.backoffice.repository.TestResultRepository;
import com.tamkeen.backoffice.repository.search.TestResultSearchRepository;
import com.tamkeen.backoffice.service.dto.TestResultDTO;
import com.tamkeen.backoffice.service.mapper.TestResultMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.TestResult}.
 */
@Service
public class TestResultService {

    private static final Logger LOG = LoggerFactory.getLogger(TestResultService.class);

    private final TestResultRepository testResultRepository;

    private final TestResultMapper testResultMapper;

    private final TestResultSearchRepository testResultSearchRepository;

    public TestResultService(
        TestResultRepository testResultRepository,
        TestResultMapper testResultMapper,
        TestResultSearchRepository testResultSearchRepository
    ) {
        this.testResultRepository = testResultRepository;
        this.testResultMapper = testResultMapper;
        this.testResultSearchRepository = testResultSearchRepository;
    }

    /**
     * Save a testResult.
     *
     * @param testResultDTO the entity to save.
     * @return the persisted entity.
     */
    public TestResultDTO save(TestResultDTO testResultDTO) {
        LOG.debug("Request to save TestResult : {}", testResultDTO);
        TestResult testResult = testResultMapper.toEntity(testResultDTO);
        testResult = testResultRepository.save(testResult);
        testResultSearchRepository.index(testResult);
        return testResultMapper.toDto(testResult);
    }

    /**
     * Update a testResult.
     *
     * @param testResultDTO the entity to save.
     * @return the persisted entity.
     */
    public TestResultDTO update(TestResultDTO testResultDTO) {
        LOG.debug("Request to update TestResult : {}", testResultDTO);
        TestResult testResult = testResultMapper.toEntity(testResultDTO);
        testResult = testResultRepository.save(testResult);
        testResultSearchRepository.index(testResult);
        return testResultMapper.toDto(testResult);
    }

    /**
     * Partially update a testResult.
     *
     * @param testResultDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TestResultDTO> partialUpdate(TestResultDTO testResultDTO) {
        LOG.debug("Request to partially update TestResult : {}", testResultDTO);

        return testResultRepository
            .findById(testResultDTO.getId())
            .map(existingTestResult -> {
                testResultMapper.partialUpdate(existingTestResult, testResultDTO);

                return existingTestResult;
            })
            .map(testResultRepository::save)
            .map(savedTestResult -> {
                testResultSearchRepository.index(savedTestResult);
                return savedTestResult;
            })
            .map(testResultMapper::toDto);
    }

    /**
     * Get all the testResults.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TestResultDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TestResults");
        return testResultRepository.findAll(pageable).map(testResultMapper::toDto);
    }

    /**
     * Get one testResult by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TestResultDTO> findOne(String id) {
        LOG.debug("Request to get TestResult : {}", id);
        return testResultRepository.findById(id).map(testResultMapper::toDto);
    }

    /**
     * Delete the testResult by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete TestResult : {}", id);
        testResultRepository.deleteById(id);
        testResultSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the testResult corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TestResultDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of TestResults for query {}", query);
        return testResultSearchRepository.search(query, pageable).map(testResultMapper::toDto);
    }
}

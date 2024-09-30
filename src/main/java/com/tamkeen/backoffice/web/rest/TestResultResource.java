package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.TestResultRepository;
import com.tamkeen.backoffice.service.TestResultService;
import com.tamkeen.backoffice.service.dto.TestResultDTO;
import com.tamkeen.backoffice.web.rest.errors.BadRequestAlertException;
import com.tamkeen.backoffice.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.tamkeen.backoffice.domain.TestResult}.
 */
@RestController
@RequestMapping("/api/test-results")
public class TestResultResource {

    private static final Logger LOG = LoggerFactory.getLogger(TestResultResource.class);

    private static final String ENTITY_NAME = "testResult";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TestResultService testResultService;

    private final TestResultRepository testResultRepository;

    public TestResultResource(TestResultService testResultService, TestResultRepository testResultRepository) {
        this.testResultService = testResultService;
        this.testResultRepository = testResultRepository;
    }

    /**
     * {@code POST  /test-results} : Create a new testResult.
     *
     * @param testResultDTO the testResultDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new testResultDTO, or with status {@code 400 (Bad Request)} if the testResult has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TestResultDTO> createTestResult(@Valid @RequestBody TestResultDTO testResultDTO) throws URISyntaxException {
        LOG.debug("REST request to save TestResult : {}", testResultDTO);
        if (testResultDTO.getId() != null) {
            throw new BadRequestAlertException("A new testResult cannot already have an ID", ENTITY_NAME, "idexists");
        }
        testResultDTO = testResultService.save(testResultDTO);
        return ResponseEntity.created(new URI("/api/test-results/" + testResultDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, testResultDTO.getId()))
            .body(testResultDTO);
    }

    /**
     * {@code PUT  /test-results/:id} : Updates an existing testResult.
     *
     * @param id the id of the testResultDTO to save.
     * @param testResultDTO the testResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testResultDTO,
     * or with status {@code 400 (Bad Request)} if the testResultDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the testResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TestResultDTO> updateTestResult(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody TestResultDTO testResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TestResult : {}, {}", id, testResultDTO);
        if (testResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        testResultDTO = testResultService.update(testResultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, testResultDTO.getId()))
            .body(testResultDTO);
    }

    /**
     * {@code PATCH  /test-results/:id} : Partial updates given fields of an existing testResult, field will ignore if it is null
     *
     * @param id the id of the testResultDTO to save.
     * @param testResultDTO the testResultDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated testResultDTO,
     * or with status {@code 400 (Bad Request)} if the testResultDTO is not valid,
     * or with status {@code 404 (Not Found)} if the testResultDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the testResultDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TestResultDTO> partialUpdateTestResult(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody TestResultDTO testResultDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TestResult partially : {}, {}", id, testResultDTO);
        if (testResultDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, testResultDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!testResultRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TestResultDTO> result = testResultService.partialUpdate(testResultDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, testResultDTO.getId())
        );
    }

    /**
     * {@code GET  /test-results} : get all the testResults.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of testResults in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TestResultDTO>> getAllTestResults(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of TestResults");
        Page<TestResultDTO> page = testResultService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /test-results/:id} : get the "id" testResult.
     *
     * @param id the id of the testResultDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the testResultDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TestResultDTO> getTestResult(@PathVariable("id") String id) {
        LOG.debug("REST request to get TestResult : {}", id);
        Optional<TestResultDTO> testResultDTO = testResultService.findOne(id);
        return ResponseUtil.wrapOrNotFound(testResultDTO);
    }

    /**
     * {@code DELETE  /test-results/:id} : delete the "id" testResult.
     *
     * @param id the id of the testResultDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestResult(@PathVariable("id") String id) {
        LOG.debug("REST request to delete TestResult : {}", id);
        testResultService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /test-results/_search?query=:query} : search for the testResult corresponding
     * to the query.
     *
     * @param query the query of the testResult search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TestResultDTO>> searchTestResults(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of TestResults for query {}", query);
        try {
            Page<TestResultDTO> page = testResultService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

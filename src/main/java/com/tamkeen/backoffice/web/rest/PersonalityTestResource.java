package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.PersonalityTestRepository;
import com.tamkeen.backoffice.service.PersonalityTestService;
import com.tamkeen.backoffice.service.dto.PersonalityTestDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.PersonalityTest}.
 */
@RestController
@RequestMapping("/api/personality-tests")
public class PersonalityTestResource {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalityTestResource.class);

    private static final String ENTITY_NAME = "personalityTest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonalityTestService personalityTestService;

    private final PersonalityTestRepository personalityTestRepository;

    public PersonalityTestResource(PersonalityTestService personalityTestService, PersonalityTestRepository personalityTestRepository) {
        this.personalityTestService = personalityTestService;
        this.personalityTestRepository = personalityTestRepository;
    }

    /**
     * {@code POST  /personality-tests} : Create a new personalityTest.
     *
     * @param personalityTestDTO the personalityTestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personalityTestDTO, or with status {@code 400 (Bad Request)} if the personalityTest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PersonalityTestDTO> createPersonalityTest(@Valid @RequestBody PersonalityTestDTO personalityTestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PersonalityTest : {}", personalityTestDTO);
        if (personalityTestDTO.getId() != null) {
            throw new BadRequestAlertException("A new personalityTest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        personalityTestDTO = personalityTestService.save(personalityTestDTO);
        return ResponseEntity.created(new URI("/api/personality-tests/" + personalityTestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, personalityTestDTO.getId()))
            .body(personalityTestDTO);
    }

    /**
     * {@code PUT  /personality-tests/:id} : Updates an existing personalityTest.
     *
     * @param id the id of the personalityTestDTO to save.
     * @param personalityTestDTO the personalityTestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalityTestDTO,
     * or with status {@code 400 (Bad Request)} if the personalityTestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personalityTestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonalityTestDTO> updatePersonalityTest(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PersonalityTestDTO personalityTestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PersonalityTest : {}, {}", id, personalityTestDTO);
        if (personalityTestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalityTestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalityTestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        personalityTestDTO = personalityTestService.update(personalityTestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalityTestDTO.getId()))
            .body(personalityTestDTO);
    }

    /**
     * {@code PATCH  /personality-tests/:id} : Partial updates given fields of an existing personalityTest, field will ignore if it is null
     *
     * @param id the id of the personalityTestDTO to save.
     * @param personalityTestDTO the personalityTestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalityTestDTO,
     * or with status {@code 400 (Bad Request)} if the personalityTestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the personalityTestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the personalityTestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PersonalityTestDTO> partialUpdatePersonalityTest(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PersonalityTestDTO personalityTestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PersonalityTest partially : {}, {}", id, personalityTestDTO);
        if (personalityTestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalityTestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalityTestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PersonalityTestDTO> result = personalityTestService.partialUpdate(personalityTestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalityTestDTO.getId())
        );
    }

    /**
     * {@code GET  /personality-tests} : get all the personalityTests.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personalityTests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PersonalityTestDTO>> getAllPersonalityTests(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PersonalityTests");
        Page<PersonalityTestDTO> page = personalityTestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /personality-tests/:id} : get the "id" personalityTest.
     *
     * @param id the id of the personalityTestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personalityTestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonalityTestDTO> getPersonalityTest(@PathVariable("id") String id) {
        LOG.debug("REST request to get PersonalityTest : {}", id);
        Optional<PersonalityTestDTO> personalityTestDTO = personalityTestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(personalityTestDTO);
    }

    /**
     * {@code DELETE  /personality-tests/:id} : delete the "id" personalityTest.
     *
     * @param id the id of the personalityTestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalityTest(@PathVariable("id") String id) {
        LOG.debug("REST request to delete PersonalityTest : {}", id);
        personalityTestService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /personality-tests/_search?query=:query} : search for the personalityTest corresponding
     * to the query.
     *
     * @param query the query of the personalityTest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PersonalityTestDTO>> searchPersonalityTests(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PersonalityTests for query {}", query);
        try {
            Page<PersonalityTestDTO> page = personalityTestService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.AnswerRepository;
import com.tamkeen.backoffice.service.AnswerService;
import com.tamkeen.backoffice.service.dto.AnswerDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.Answer}.
 */
@RestController
@RequestMapping("/api/answers")
public class AnswerResource {

    private static final Logger LOG = LoggerFactory.getLogger(AnswerResource.class);

    private static final String ENTITY_NAME = "answer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnswerService answerService;

    private final AnswerRepository answerRepository;

    public AnswerResource(AnswerService answerService, AnswerRepository answerRepository) {
        this.answerService = answerService;
        this.answerRepository = answerRepository;
    }

    /**
     * {@code POST  /answers} : Create a new answer.
     *
     * @param answerDTO the answerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new answerDTO, or with status {@code 400 (Bad Request)} if the answer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AnswerDTO> createAnswer(@Valid @RequestBody AnswerDTO answerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Answer : {}", answerDTO);
        if (answerDTO.getId() != null) {
            throw new BadRequestAlertException("A new answer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        answerDTO = answerService.save(answerDTO);
        return ResponseEntity.created(new URI("/api/answers/" + answerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, answerDTO.getId()))
            .body(answerDTO);
    }

    /**
     * {@code PUT  /answers/:id} : Updates an existing answer.
     *
     * @param id the id of the answerDTO to save.
     * @param answerDTO the answerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated answerDTO,
     * or with status {@code 400 (Bad Request)} if the answerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the answerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AnswerDTO> updateAnswer(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody AnswerDTO answerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Answer : {}, {}", id, answerDTO);
        if (answerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, answerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!answerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        answerDTO = answerService.update(answerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, answerDTO.getId()))
            .body(answerDTO);
    }

    /**
     * {@code PATCH  /answers/:id} : Partial updates given fields of an existing answer, field will ignore if it is null
     *
     * @param id the id of the answerDTO to save.
     * @param answerDTO the answerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated answerDTO,
     * or with status {@code 400 (Bad Request)} if the answerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the answerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the answerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AnswerDTO> partialUpdateAnswer(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody AnswerDTO answerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Answer partially : {}, {}", id, answerDTO);
        if (answerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, answerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!answerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AnswerDTO> result = answerService.partialUpdate(answerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, answerDTO.getId())
        );
    }

    /**
     * {@code GET  /answers} : get all the answers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of answers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AnswerDTO>> getAllAnswers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Answers");
        Page<AnswerDTO> page = answerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /answers/:id} : get the "id" answer.
     *
     * @param id the id of the answerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the answerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnswerDTO> getAnswer(@PathVariable("id") String id) {
        LOG.debug("REST request to get Answer : {}", id);
        Optional<AnswerDTO> answerDTO = answerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(answerDTO);
    }

    /**
     * {@code DELETE  /answers/:id} : delete the "id" answer.
     *
     * @param id the id of the answerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Answer : {}", id);
        answerService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /answers/_search?query=:query} : search for the answer corresponding
     * to the query.
     *
     * @param query the query of the answer search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<AnswerDTO>> searchAnswers(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Answers for query {}", query);
        try {
            Page<AnswerDTO> page = answerService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

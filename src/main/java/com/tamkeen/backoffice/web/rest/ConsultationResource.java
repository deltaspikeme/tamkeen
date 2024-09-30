package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.ConsultationRepository;
import com.tamkeen.backoffice.service.ConsultationService;
import com.tamkeen.backoffice.service.dto.ConsultationDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.Consultation}.
 */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultationResource.class);

    private static final String ENTITY_NAME = "consultation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConsultationService consultationService;

    private final ConsultationRepository consultationRepository;

    public ConsultationResource(ConsultationService consultationService, ConsultationRepository consultationRepository) {
        this.consultationService = consultationService;
        this.consultationRepository = consultationRepository;
    }

    /**
     * {@code POST  /consultations} : Create a new consultation.
     *
     * @param consultationDTO the consultationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new consultationDTO, or with status {@code 400 (Bad Request)} if the consultation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConsultationDTO> createConsultation(@Valid @RequestBody ConsultationDTO consultationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Consultation : {}", consultationDTO);
        if (consultationDTO.getId() != null) {
            throw new BadRequestAlertException("A new consultation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        consultationDTO = consultationService.save(consultationDTO);
        return ResponseEntity.created(new URI("/api/consultations/" + consultationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, consultationDTO.getId()))
            .body(consultationDTO);
    }

    /**
     * {@code PUT  /consultations/:id} : Updates an existing consultation.
     *
     * @param id the id of the consultationDTO to save.
     * @param consultationDTO the consultationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consultationDTO,
     * or with status {@code 400 (Bad Request)} if the consultationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the consultationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDTO> updateConsultation(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ConsultationDTO consultationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Consultation : {}, {}", id, consultationDTO);
        if (consultationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consultationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consultationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        consultationDTO = consultationService.update(consultationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consultationDTO.getId()))
            .body(consultationDTO);
    }

    /**
     * {@code PATCH  /consultations/:id} : Partial updates given fields of an existing consultation, field will ignore if it is null
     *
     * @param id the id of the consultationDTO to save.
     * @param consultationDTO the consultationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consultationDTO,
     * or with status {@code 400 (Bad Request)} if the consultationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the consultationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the consultationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConsultationDTO> partialUpdateConsultation(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ConsultationDTO consultationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Consultation partially : {}, {}", id, consultationDTO);
        if (consultationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consultationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consultationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConsultationDTO> result = consultationService.partialUpdate(consultationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consultationDTO.getId())
        );
    }

    /**
     * {@code GET  /consultations} : get all the consultations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of consultations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ConsultationDTO>> getAllConsultations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Consultations");
        Page<ConsultationDTO> page = consultationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /consultations/:id} : get the "id" consultation.
     *
     * @param id the id of the consultationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the consultationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDTO> getConsultation(@PathVariable("id") String id) {
        LOG.debug("REST request to get Consultation : {}", id);
        Optional<ConsultationDTO> consultationDTO = consultationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(consultationDTO);
    }

    /**
     * {@code DELETE  /consultations/:id} : delete the "id" consultation.
     *
     * @param id the id of the consultationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Consultation : {}", id);
        consultationService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /consultations/_search?query=:query} : search for the consultation corresponding
     * to the query.
     *
     * @param query the query of the consultation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ConsultationDTO>> searchConsultations(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Consultations for query {}", query);
        try {
            Page<ConsultationDTO> page = consultationService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

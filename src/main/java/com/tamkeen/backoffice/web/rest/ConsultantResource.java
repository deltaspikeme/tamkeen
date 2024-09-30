package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.ConsultantRepository;
import com.tamkeen.backoffice.service.ConsultantService;
import com.tamkeen.backoffice.service.dto.ConsultantDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.Consultant}.
 */
@RestController
@RequestMapping("/api/consultants")
public class ConsultantResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConsultantResource.class);

    private static final String ENTITY_NAME = "consultant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConsultantService consultantService;

    private final ConsultantRepository consultantRepository;

    public ConsultantResource(ConsultantService consultantService, ConsultantRepository consultantRepository) {
        this.consultantService = consultantService;
        this.consultantRepository = consultantRepository;
    }

    /**
     * {@code POST  /consultants} : Create a new consultant.
     *
     * @param consultantDTO the consultantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new consultantDTO, or with status {@code 400 (Bad Request)} if the consultant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConsultantDTO> createConsultant(@Valid @RequestBody ConsultantDTO consultantDTO) throws URISyntaxException {
        LOG.debug("REST request to save Consultant : {}", consultantDTO);
        if (consultantDTO.getId() != null) {
            throw new BadRequestAlertException("A new consultant cannot already have an ID", ENTITY_NAME, "idexists");
        }
        consultantDTO = consultantService.save(consultantDTO);
        return ResponseEntity.created(new URI("/api/consultants/" + consultantDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, consultantDTO.getId()))
            .body(consultantDTO);
    }

    /**
     * {@code PUT  /consultants/:id} : Updates an existing consultant.
     *
     * @param id the id of the consultantDTO to save.
     * @param consultantDTO the consultantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consultantDTO,
     * or with status {@code 400 (Bad Request)} if the consultantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the consultantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConsultantDTO> updateConsultant(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ConsultantDTO consultantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Consultant : {}, {}", id, consultantDTO);
        if (consultantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consultantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consultantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        consultantDTO = consultantService.update(consultantDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consultantDTO.getId()))
            .body(consultantDTO);
    }

    /**
     * {@code PATCH  /consultants/:id} : Partial updates given fields of an existing consultant, field will ignore if it is null
     *
     * @param id the id of the consultantDTO to save.
     * @param consultantDTO the consultantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consultantDTO,
     * or with status {@code 400 (Bad Request)} if the consultantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the consultantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the consultantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConsultantDTO> partialUpdateConsultant(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ConsultantDTO consultantDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Consultant partially : {}, {}", id, consultantDTO);
        if (consultantDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consultantDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consultantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConsultantDTO> result = consultantService.partialUpdate(consultantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consultantDTO.getId())
        );
    }

    /**
     * {@code GET  /consultants} : get all the consultants.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of consultants in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ConsultantDTO>> getAllConsultants(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Consultants");
        Page<ConsultantDTO> page = consultantService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /consultants/:id} : get the "id" consultant.
     *
     * @param id the id of the consultantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the consultantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsultantDTO> getConsultant(@PathVariable("id") String id) {
        LOG.debug("REST request to get Consultant : {}", id);
        Optional<ConsultantDTO> consultantDTO = consultantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(consultantDTO);
    }

    /**
     * {@code DELETE  /consultants/:id} : delete the "id" consultant.
     *
     * @param id the id of the consultantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultant(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Consultant : {}", id);
        consultantService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /consultants/_search?query=:query} : search for the consultant corresponding
     * to the query.
     *
     * @param query the query of the consultant search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<ConsultantDTO>> searchConsultants(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Consultants for query {}", query);
        try {
            Page<ConsultantDTO> page = consultantService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

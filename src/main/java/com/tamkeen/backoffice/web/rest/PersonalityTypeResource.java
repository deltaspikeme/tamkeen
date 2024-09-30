package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.PersonalityTypeRepository;
import com.tamkeen.backoffice.service.PersonalityTypeService;
import com.tamkeen.backoffice.service.dto.PersonalityTypeDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.PersonalityType}.
 */
@RestController
@RequestMapping("/api/personality-types")
public class PersonalityTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(PersonalityTypeResource.class);

    private static final String ENTITY_NAME = "personalityType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PersonalityTypeService personalityTypeService;

    private final PersonalityTypeRepository personalityTypeRepository;

    public PersonalityTypeResource(PersonalityTypeService personalityTypeService, PersonalityTypeRepository personalityTypeRepository) {
        this.personalityTypeService = personalityTypeService;
        this.personalityTypeRepository = personalityTypeRepository;
    }

    /**
     * {@code POST  /personality-types} : Create a new personalityType.
     *
     * @param personalityTypeDTO the personalityTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new personalityTypeDTO, or with status {@code 400 (Bad Request)} if the personalityType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PersonalityTypeDTO> createPersonalityType(@Valid @RequestBody PersonalityTypeDTO personalityTypeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PersonalityType : {}", personalityTypeDTO);
        if (personalityTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new personalityType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        personalityTypeDTO = personalityTypeService.save(personalityTypeDTO);
        return ResponseEntity.created(new URI("/api/personality-types/" + personalityTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, personalityTypeDTO.getId()))
            .body(personalityTypeDTO);
    }

    /**
     * {@code PUT  /personality-types/:id} : Updates an existing personalityType.
     *
     * @param id the id of the personalityTypeDTO to save.
     * @param personalityTypeDTO the personalityTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalityTypeDTO,
     * or with status {@code 400 (Bad Request)} if the personalityTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the personalityTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonalityTypeDTO> updatePersonalityType(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PersonalityTypeDTO personalityTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PersonalityType : {}, {}", id, personalityTypeDTO);
        if (personalityTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalityTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalityTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        personalityTypeDTO = personalityTypeService.update(personalityTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalityTypeDTO.getId()))
            .body(personalityTypeDTO);
    }

    /**
     * {@code PATCH  /personality-types/:id} : Partial updates given fields of an existing personalityType, field will ignore if it is null
     *
     * @param id the id of the personalityTypeDTO to save.
     * @param personalityTypeDTO the personalityTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated personalityTypeDTO,
     * or with status {@code 400 (Bad Request)} if the personalityTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the personalityTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the personalityTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PersonalityTypeDTO> partialUpdatePersonalityType(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PersonalityTypeDTO personalityTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PersonalityType partially : {}, {}", id, personalityTypeDTO);
        if (personalityTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, personalityTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!personalityTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PersonalityTypeDTO> result = personalityTypeService.partialUpdate(personalityTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, personalityTypeDTO.getId())
        );
    }

    /**
     * {@code GET  /personality-types} : get all the personalityTypes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of personalityTypes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PersonalityTypeDTO>> getAllPersonalityTypes(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PersonalityTypes");
        Page<PersonalityTypeDTO> page = personalityTypeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /personality-types/:id} : get the "id" personalityType.
     *
     * @param id the id of the personalityTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the personalityTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonalityTypeDTO> getPersonalityType(@PathVariable("id") String id) {
        LOG.debug("REST request to get PersonalityType : {}", id);
        Optional<PersonalityTypeDTO> personalityTypeDTO = personalityTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(personalityTypeDTO);
    }

    /**
     * {@code DELETE  /personality-types/:id} : delete the "id" personalityType.
     *
     * @param id the id of the personalityTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalityType(@PathVariable("id") String id) {
        LOG.debug("REST request to delete PersonalityType : {}", id);
        personalityTypeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /personality-types/_search?query=:query} : search for the personalityType corresponding
     * to the query.
     *
     * @param query the query of the personalityType search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<PersonalityTypeDTO>> searchPersonalityTypes(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of PersonalityTypes for query {}", query);
        try {
            Page<PersonalityTypeDTO> page = personalityTypeService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

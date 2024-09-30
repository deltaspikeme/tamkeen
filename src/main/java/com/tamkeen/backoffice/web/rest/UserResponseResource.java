package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.UserResponseRepository;
import com.tamkeen.backoffice.service.UserResponseService;
import com.tamkeen.backoffice.service.dto.UserResponseDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.UserResponse}.
 */
@RestController
@RequestMapping("/api/user-responses")
public class UserResponseResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResponseResource.class);

    private static final String ENTITY_NAME = "userResponse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserResponseService userResponseService;

    private final UserResponseRepository userResponseRepository;

    public UserResponseResource(UserResponseService userResponseService, UserResponseRepository userResponseRepository) {
        this.userResponseService = userResponseService;
        this.userResponseRepository = userResponseRepository;
    }

    /**
     * {@code POST  /user-responses} : Create a new userResponse.
     *
     * @param userResponseDTO the userResponseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userResponseDTO, or with status {@code 400 (Bad Request)} if the userResponse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserResponseDTO> createUserResponse(@Valid @RequestBody UserResponseDTO userResponseDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserResponse : {}", userResponseDTO);
        if (userResponseDTO.getId() != null) {
            throw new BadRequestAlertException("A new userResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userResponseDTO = userResponseService.save(userResponseDTO);
        return ResponseEntity.created(new URI("/api/user-responses/" + userResponseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userResponseDTO.getId()))
            .body(userResponseDTO);
    }

    /**
     * {@code PUT  /user-responses/:id} : Updates an existing userResponse.
     *
     * @param id the id of the userResponseDTO to save.
     * @param userResponseDTO the userResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userResponseDTO,
     * or with status {@code 400 (Bad Request)} if the userResponseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserResponse(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody UserResponseDTO userResponseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserResponse : {}, {}", id, userResponseDTO);
        if (userResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userResponseDTO = userResponseService.update(userResponseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userResponseDTO.getId()))
            .body(userResponseDTO);
    }

    /**
     * {@code PATCH  /user-responses/:id} : Partial updates given fields of an existing userResponse, field will ignore if it is null
     *
     * @param id the id of the userResponseDTO to save.
     * @param userResponseDTO the userResponseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userResponseDTO,
     * or with status {@code 400 (Bad Request)} if the userResponseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userResponseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userResponseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserResponseDTO> partialUpdateUserResponse(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody UserResponseDTO userResponseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserResponse partially : {}, {}", id, userResponseDTO);
        if (userResponseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userResponseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserResponseDTO> result = userResponseService.partialUpdate(userResponseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userResponseDTO.getId())
        );
    }

    /**
     * {@code GET  /user-responses} : get all the userResponses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userResponses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserResponseDTO>> getAllUserResponses(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of UserResponses");
        Page<UserResponseDTO> page = userResponseService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-responses/:id} : get the "id" userResponse.
     *
     * @param id the id of the userResponseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userResponseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserResponse(@PathVariable("id") String id) {
        LOG.debug("REST request to get UserResponse : {}", id);
        Optional<UserResponseDTO> userResponseDTO = userResponseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userResponseDTO);
    }

    /**
     * {@code DELETE  /user-responses/:id} : delete the "id" userResponse.
     *
     * @param id the id of the userResponseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserResponse(@PathVariable("id") String id) {
        LOG.debug("REST request to delete UserResponse : {}", id);
        userResponseService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /user-responses/_search?query=:query} : search for the userResponse corresponding
     * to the query.
     *
     * @param query the query of the userResponse search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UserResponseDTO>> searchUserResponses(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of UserResponses for query {}", query);
        try {
            Page<UserResponseDTO> page = userResponseService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

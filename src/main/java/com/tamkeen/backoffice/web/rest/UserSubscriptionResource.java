package com.tamkeen.backoffice.web.rest;

import com.tamkeen.backoffice.repository.UserSubscriptionRepository;
import com.tamkeen.backoffice.service.UserSubscriptionService;
import com.tamkeen.backoffice.service.dto.UserSubscriptionDTO;
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
 * REST controller for managing {@link com.tamkeen.backoffice.domain.UserSubscription}.
 */
@RestController
@RequestMapping("/api/user-subscriptions")
public class UserSubscriptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserSubscriptionResource.class);

    private static final String ENTITY_NAME = "userSubscription";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserSubscriptionService userSubscriptionService;

    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscriptionResource(
        UserSubscriptionService userSubscriptionService,
        UserSubscriptionRepository userSubscriptionRepository
    ) {
        this.userSubscriptionService = userSubscriptionService;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    /**
     * {@code POST  /user-subscriptions} : Create a new userSubscription.
     *
     * @param userSubscriptionDTO the userSubscriptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userSubscriptionDTO, or with status {@code 400 (Bad Request)} if the userSubscription has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<UserSubscriptionDTO> createUserSubscription(@Valid @RequestBody UserSubscriptionDTO userSubscriptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save UserSubscription : {}", userSubscriptionDTO);
        if (userSubscriptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new userSubscription cannot already have an ID", ENTITY_NAME, "idexists");
        }
        userSubscriptionDTO = userSubscriptionService.save(userSubscriptionDTO);
        return ResponseEntity.created(new URI("/api/user-subscriptions/" + userSubscriptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, userSubscriptionDTO.getId()))
            .body(userSubscriptionDTO);
    }

    /**
     * {@code PUT  /user-subscriptions/:id} : Updates an existing userSubscription.
     *
     * @param id the id of the userSubscriptionDTO to save.
     * @param userSubscriptionDTO the userSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the userSubscriptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserSubscriptionDTO> updateUserSubscription(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody UserSubscriptionDTO userSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UserSubscription : {}, {}", id, userSubscriptionDTO);
        if (userSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userSubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        userSubscriptionDTO = userSubscriptionService.update(userSubscriptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSubscriptionDTO.getId()))
            .body(userSubscriptionDTO);
    }

    /**
     * {@code PATCH  /user-subscriptions/:id} : Partial updates given fields of an existing userSubscription, field will ignore if it is null
     *
     * @param id the id of the userSubscriptionDTO to save.
     * @param userSubscriptionDTO the userSubscriptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userSubscriptionDTO,
     * or with status {@code 400 (Bad Request)} if the userSubscriptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userSubscriptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userSubscriptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<UserSubscriptionDTO> partialUpdateUserSubscription(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody UserSubscriptionDTO userSubscriptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UserSubscription partially : {}, {}", id, userSubscriptionDTO);
        if (userSubscriptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userSubscriptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!userSubscriptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UserSubscriptionDTO> result = userSubscriptionService.partialUpdate(userSubscriptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userSubscriptionDTO.getId())
        );
    }

    /**
     * {@code GET  /user-subscriptions} : get all the userSubscriptions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userSubscriptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UserSubscriptionDTO>> getAllUserSubscriptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of UserSubscriptions");
        Page<UserSubscriptionDTO> page = userSubscriptionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /user-subscriptions/:id} : get the "id" userSubscription.
     *
     * @param id the id of the userSubscriptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userSubscriptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserSubscriptionDTO> getUserSubscription(@PathVariable("id") String id) {
        LOG.debug("REST request to get UserSubscription : {}", id);
        Optional<UserSubscriptionDTO> userSubscriptionDTO = userSubscriptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userSubscriptionDTO);
    }

    /**
     * {@code DELETE  /user-subscriptions/:id} : delete the "id" userSubscription.
     *
     * @param id the id of the userSubscriptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSubscription(@PathVariable("id") String id) {
        LOG.debug("REST request to delete UserSubscription : {}", id);
        userSubscriptionService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /user-subscriptions/_search?query=:query} : search for the userSubscription corresponding
     * to the query.
     *
     * @param query the query of the userSubscription search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<UserSubscriptionDTO>> searchUserSubscriptions(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of UserSubscriptions for query {}", query);
        try {
            Page<UserSubscriptionDTO> page = userSubscriptionService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}

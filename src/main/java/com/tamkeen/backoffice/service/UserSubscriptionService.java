package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.UserSubscription;
import com.tamkeen.backoffice.repository.UserSubscriptionRepository;
import com.tamkeen.backoffice.repository.search.UserSubscriptionSearchRepository;
import com.tamkeen.backoffice.service.dto.UserSubscriptionDTO;
import com.tamkeen.backoffice.service.mapper.UserSubscriptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.UserSubscription}.
 */
@Service
public class UserSubscriptionService {

    private static final Logger LOG = LoggerFactory.getLogger(UserSubscriptionService.class);

    private final UserSubscriptionRepository userSubscriptionRepository;

    private final UserSubscriptionMapper userSubscriptionMapper;

    private final UserSubscriptionSearchRepository userSubscriptionSearchRepository;

    public UserSubscriptionService(
        UserSubscriptionRepository userSubscriptionRepository,
        UserSubscriptionMapper userSubscriptionMapper,
        UserSubscriptionSearchRepository userSubscriptionSearchRepository
    ) {
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.userSubscriptionMapper = userSubscriptionMapper;
        this.userSubscriptionSearchRepository = userSubscriptionSearchRepository;
    }

    /**
     * Save a userSubscription.
     *
     * @param userSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    public UserSubscriptionDTO save(UserSubscriptionDTO userSubscriptionDTO) {
        LOG.debug("Request to save UserSubscription : {}", userSubscriptionDTO);
        UserSubscription userSubscription = userSubscriptionMapper.toEntity(userSubscriptionDTO);
        userSubscription = userSubscriptionRepository.save(userSubscription);
        userSubscriptionSearchRepository.index(userSubscription);
        return userSubscriptionMapper.toDto(userSubscription);
    }

    /**
     * Update a userSubscription.
     *
     * @param userSubscriptionDTO the entity to save.
     * @return the persisted entity.
     */
    public UserSubscriptionDTO update(UserSubscriptionDTO userSubscriptionDTO) {
        LOG.debug("Request to update UserSubscription : {}", userSubscriptionDTO);
        UserSubscription userSubscription = userSubscriptionMapper.toEntity(userSubscriptionDTO);
        userSubscription = userSubscriptionRepository.save(userSubscription);
        userSubscriptionSearchRepository.index(userSubscription);
        return userSubscriptionMapper.toDto(userSubscription);
    }

    /**
     * Partially update a userSubscription.
     *
     * @param userSubscriptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserSubscriptionDTO> partialUpdate(UserSubscriptionDTO userSubscriptionDTO) {
        LOG.debug("Request to partially update UserSubscription : {}", userSubscriptionDTO);

        return userSubscriptionRepository
            .findById(userSubscriptionDTO.getId())
            .map(existingUserSubscription -> {
                userSubscriptionMapper.partialUpdate(existingUserSubscription, userSubscriptionDTO);

                return existingUserSubscription;
            })
            .map(userSubscriptionRepository::save)
            .map(savedUserSubscription -> {
                userSubscriptionSearchRepository.index(savedUserSubscription);
                return savedUserSubscription;
            })
            .map(userSubscriptionMapper::toDto);
    }

    /**
     * Get all the userSubscriptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<UserSubscriptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserSubscriptions");
        return userSubscriptionRepository.findAll(pageable).map(userSubscriptionMapper::toDto);
    }

    /**
     * Get one userSubscription by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<UserSubscriptionDTO> findOne(String id) {
        LOG.debug("Request to get UserSubscription : {}", id);
        return userSubscriptionRepository.findById(id).map(userSubscriptionMapper::toDto);
    }

    /**
     * Delete the userSubscription by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete UserSubscription : {}", id);
        userSubscriptionRepository.deleteById(id);
        userSubscriptionSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the userSubscription corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<UserSubscriptionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of UserSubscriptions for query {}", query);
        return userSubscriptionSearchRepository.search(query, pageable).map(userSubscriptionMapper::toDto);
    }
}

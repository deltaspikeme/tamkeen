package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.UserResponse;
import com.tamkeen.backoffice.repository.UserResponseRepository;
import com.tamkeen.backoffice.repository.search.UserResponseSearchRepository;
import com.tamkeen.backoffice.service.dto.UserResponseDTO;
import com.tamkeen.backoffice.service.mapper.UserResponseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.UserResponse}.
 */
@Service
public class UserResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(UserResponseService.class);

    private final UserResponseRepository userResponseRepository;

    private final UserResponseMapper userResponseMapper;

    private final UserResponseSearchRepository userResponseSearchRepository;

    public UserResponseService(
        UserResponseRepository userResponseRepository,
        UserResponseMapper userResponseMapper,
        UserResponseSearchRepository userResponseSearchRepository
    ) {
        this.userResponseRepository = userResponseRepository;
        this.userResponseMapper = userResponseMapper;
        this.userResponseSearchRepository = userResponseSearchRepository;
    }

    /**
     * Save a userResponse.
     *
     * @param userResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public UserResponseDTO save(UserResponseDTO userResponseDTO) {
        LOG.debug("Request to save UserResponse : {}", userResponseDTO);
        UserResponse userResponse = userResponseMapper.toEntity(userResponseDTO);
        userResponse = userResponseRepository.save(userResponse);
        userResponseSearchRepository.index(userResponse);
        return userResponseMapper.toDto(userResponse);
    }

    /**
     * Update a userResponse.
     *
     * @param userResponseDTO the entity to save.
     * @return the persisted entity.
     */
    public UserResponseDTO update(UserResponseDTO userResponseDTO) {
        LOG.debug("Request to update UserResponse : {}", userResponseDTO);
        UserResponse userResponse = userResponseMapper.toEntity(userResponseDTO);
        userResponse = userResponseRepository.save(userResponse);
        userResponseSearchRepository.index(userResponse);
        return userResponseMapper.toDto(userResponse);
    }

    /**
     * Partially update a userResponse.
     *
     * @param userResponseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserResponseDTO> partialUpdate(UserResponseDTO userResponseDTO) {
        LOG.debug("Request to partially update UserResponse : {}", userResponseDTO);

        return userResponseRepository
            .findById(userResponseDTO.getId())
            .map(existingUserResponse -> {
                userResponseMapper.partialUpdate(existingUserResponse, userResponseDTO);

                return existingUserResponse;
            })
            .map(userResponseRepository::save)
            .map(savedUserResponse -> {
                userResponseSearchRepository.index(savedUserResponse);
                return savedUserResponse;
            })
            .map(userResponseMapper::toDto);
    }

    /**
     * Get all the userResponses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<UserResponseDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserResponses");
        return userResponseRepository.findAll(pageable).map(userResponseMapper::toDto);
    }

    /**
     * Get one userResponse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<UserResponseDTO> findOne(String id) {
        LOG.debug("Request to get UserResponse : {}", id);
        return userResponseRepository.findById(id).map(userResponseMapper::toDto);
    }

    /**
     * Delete the userResponse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete UserResponse : {}", id);
        userResponseRepository.deleteById(id);
        userResponseSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the userResponse corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<UserResponseDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of UserResponses for query {}", query);
        return userResponseSearchRepository.search(query, pageable).map(userResponseMapper::toDto);
    }
}

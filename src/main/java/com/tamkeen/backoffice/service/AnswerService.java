package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.Answer;
import com.tamkeen.backoffice.repository.AnswerRepository;
import com.tamkeen.backoffice.repository.search.AnswerSearchRepository;
import com.tamkeen.backoffice.service.dto.AnswerDTO;
import com.tamkeen.backoffice.service.mapper.AnswerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.Answer}.
 */
@Service
public class AnswerService {

    private static final Logger LOG = LoggerFactory.getLogger(AnswerService.class);

    private final AnswerRepository answerRepository;

    private final AnswerMapper answerMapper;

    private final AnswerSearchRepository answerSearchRepository;

    public AnswerService(AnswerRepository answerRepository, AnswerMapper answerMapper, AnswerSearchRepository answerSearchRepository) {
        this.answerRepository = answerRepository;
        this.answerMapper = answerMapper;
        this.answerSearchRepository = answerSearchRepository;
    }

    /**
     * Save a answer.
     *
     * @param answerDTO the entity to save.
     * @return the persisted entity.
     */
    public AnswerDTO save(AnswerDTO answerDTO) {
        LOG.debug("Request to save Answer : {}", answerDTO);
        Answer answer = answerMapper.toEntity(answerDTO);
        answer = answerRepository.save(answer);
        answerSearchRepository.index(answer);
        return answerMapper.toDto(answer);
    }

    /**
     * Update a answer.
     *
     * @param answerDTO the entity to save.
     * @return the persisted entity.
     */
    public AnswerDTO update(AnswerDTO answerDTO) {
        LOG.debug("Request to update Answer : {}", answerDTO);
        Answer answer = answerMapper.toEntity(answerDTO);
        answer = answerRepository.save(answer);
        answerSearchRepository.index(answer);
        return answerMapper.toDto(answer);
    }

    /**
     * Partially update a answer.
     *
     * @param answerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AnswerDTO> partialUpdate(AnswerDTO answerDTO) {
        LOG.debug("Request to partially update Answer : {}", answerDTO);

        return answerRepository
            .findById(answerDTO.getId())
            .map(existingAnswer -> {
                answerMapper.partialUpdate(existingAnswer, answerDTO);

                return existingAnswer;
            })
            .map(answerRepository::save)
            .map(savedAnswer -> {
                answerSearchRepository.index(savedAnswer);
                return savedAnswer;
            })
            .map(answerMapper::toDto);
    }

    /**
     * Get all the answers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<AnswerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Answers");
        return answerRepository.findAll(pageable).map(answerMapper::toDto);
    }

    /**
     * Get one answer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<AnswerDTO> findOne(String id) {
        LOG.debug("Request to get Answer : {}", id);
        return answerRepository.findById(id).map(answerMapper::toDto);
    }

    /**
     * Delete the answer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Answer : {}", id);
        answerRepository.deleteById(id);
        answerSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the answer corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<AnswerDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Answers for query {}", query);
        return answerSearchRepository.search(query, pageable).map(answerMapper::toDto);
    }
}

package com.tamkeen.backoffice.service;

import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.domain.Question;
import com.tamkeen.backoffice.repository.QuestionRepository;
import com.tamkeen.backoffice.repository.search.QuestionSearchRepository;
import com.tamkeen.backoffice.service.dto.QuestionDTO;
import com.tamkeen.backoffice.service.mapper.QuestionMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.tamkeen.backoffice.domain.Question}.
 */
@Service
public class QuestionService {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionService.class);

    private final QuestionRepository questionRepository;

    private final QuestionMapper questionMapper;

    private final QuestionSearchRepository questionSearchRepository;

    public QuestionService(
        QuestionRepository questionRepository,
        QuestionMapper questionMapper,
        QuestionSearchRepository questionSearchRepository
    ) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.questionSearchRepository = questionSearchRepository;
    }

    /**
     * Save a question.
     *
     * @param questionDTO the entity to save.
     * @return the persisted entity.
     */
    public QuestionDTO save(QuestionDTO questionDTO) {
        LOG.debug("Request to save Question : {}", questionDTO);
        Question question = questionMapper.toEntity(questionDTO);
        question = questionRepository.save(question);
        questionSearchRepository.index(question);
        return questionMapper.toDto(question);
    }

    /**
     * Update a question.
     *
     * @param questionDTO the entity to save.
     * @return the persisted entity.
     */
    public QuestionDTO update(QuestionDTO questionDTO) {
        LOG.debug("Request to update Question : {}", questionDTO);
        Question question = questionMapper.toEntity(questionDTO);
        question = questionRepository.save(question);
        questionSearchRepository.index(question);
        return questionMapper.toDto(question);
    }

    /**
     * Partially update a question.
     *
     * @param questionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<QuestionDTO> partialUpdate(QuestionDTO questionDTO) {
        LOG.debug("Request to partially update Question : {}", questionDTO);

        return questionRepository
            .findById(questionDTO.getId())
            .map(existingQuestion -> {
                questionMapper.partialUpdate(existingQuestion, questionDTO);

                return existingQuestion;
            })
            .map(questionRepository::save)
            .map(savedQuestion -> {
                questionSearchRepository.index(savedQuestion);
                return savedQuestion;
            })
            .map(questionMapper::toDto);
    }

    /**
     * Get all the questions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<QuestionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Questions");
        return questionRepository.findAll(pageable).map(questionMapper::toDto);
    }

    /**
     * Get one question by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<QuestionDTO> findOne(String id) {
        LOG.debug("Request to get Question : {}", id);
        return questionRepository.findById(id).map(questionMapper::toDto);
    }

    /**
     * Delete the question by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        LOG.debug("Request to delete Question : {}", id);
        questionRepository.deleteById(id);
        questionSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the question corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<QuestionDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Questions for query {}", query);
        return questionSearchRepository.search(query, pageable).map(questionMapper::toDto);
    }

    public Optional<List<QuestionDTO>> findByPersonalityTest(PersonalityTest personalityTest) {
        return questionRepository.findByPersonalityTest(personalityTest).map(questionMapper::toDto);
    }
}

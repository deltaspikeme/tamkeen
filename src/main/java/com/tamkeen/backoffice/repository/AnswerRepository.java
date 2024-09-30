package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Answer entity.
 */
@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {}

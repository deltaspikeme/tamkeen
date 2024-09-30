package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.PersonalityTest;
import com.tamkeen.backoffice.domain.Question;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Question entity.
 */
@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    Optional<List<Question>> findByPersonalityTest(PersonalityTest personalityTest);
}

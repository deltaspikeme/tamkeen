package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.PersonalityTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the PersonalityTest entity.
 */
@Repository
public interface PersonalityTestRepository extends MongoRepository<PersonalityTest, String> {}

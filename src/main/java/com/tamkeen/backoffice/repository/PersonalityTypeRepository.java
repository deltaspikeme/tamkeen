package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.PersonalityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the PersonalityType entity.
 */
@Repository
public interface PersonalityTypeRepository extends MongoRepository<PersonalityType, String> {}

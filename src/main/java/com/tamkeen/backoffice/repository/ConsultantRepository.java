package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.Consultant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Consultant entity.
 */
@Repository
public interface ConsultantRepository extends MongoRepository<Consultant, String> {}

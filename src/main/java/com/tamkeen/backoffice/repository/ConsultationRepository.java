package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.Consultation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Consultation entity.
 */
@Repository
public interface ConsultationRepository extends MongoRepository<Consultation, String> {}

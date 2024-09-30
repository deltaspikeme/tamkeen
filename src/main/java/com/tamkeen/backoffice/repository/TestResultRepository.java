package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.TestResult;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the TestResult entity.
 */
@Repository
public interface TestResultRepository extends MongoRepository<TestResult, String> {}

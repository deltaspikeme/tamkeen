package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.UserResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the UserResponse entity.
 */
@Repository
public interface UserResponseRepository extends MongoRepository<UserResponse, String> {}

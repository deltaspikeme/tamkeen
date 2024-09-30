package com.tamkeen.backoffice.repository;

import com.tamkeen.backoffice.domain.UserSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the UserSubscription entity.
 */
@Repository
public interface UserSubscriptionRepository extends MongoRepository<UserSubscription, String> {}

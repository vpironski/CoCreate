package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String identifier);
    Optional<User> findByEmail(String identifier);
}

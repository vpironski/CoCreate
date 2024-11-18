package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}

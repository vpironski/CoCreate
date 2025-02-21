package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findAllByOwner(User owner);
    Optional<Project> findProjectByIdAndOwner(String id, User user);
}

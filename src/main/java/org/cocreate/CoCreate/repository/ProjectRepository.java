package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findAllByOwnerId(String ownerId);
    Optional<Project> findProjectByIdAndOwnerId(String id, String ownerId);
}

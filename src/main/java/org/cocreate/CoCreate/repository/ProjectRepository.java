package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.Project;
import org.cocreate.CoCreate.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findAllByOwner(User owner);
    Project findProjectByIdAndOwner(String id, User user);
}

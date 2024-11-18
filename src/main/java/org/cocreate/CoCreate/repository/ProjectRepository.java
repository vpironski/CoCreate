package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
}

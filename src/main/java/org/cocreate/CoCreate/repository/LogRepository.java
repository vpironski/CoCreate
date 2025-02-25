package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<LogEntry, String> {
}

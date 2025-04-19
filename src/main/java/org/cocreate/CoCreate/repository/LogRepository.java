package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<LogEntry, String> {
    List<LogEntry> findAllByUserId(String userId);
}

package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findAllByUserId(String userId);
    Optional<AuditLog> findByEntityIdAndUserId(String entityId, String userId);
}

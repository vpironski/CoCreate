package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface AuditRepository extends MongoRepository<AuditLog, String> {
    Optional<AuditLog> findByEntityIdAndUserId(String entityId, String userId);
}

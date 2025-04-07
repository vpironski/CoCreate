package org.cocreate.CoCreate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_log")
public class AuditLog {
    @Id
    private String id;
    private String entityId;
    private String userId;
    private LocalDateTime deletedAt;
    private Object originalData; // Stores the full deleted object
}

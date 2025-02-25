package org.cocreate.CoCreate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "logs")
public class LogEntry {
    @Id
    private String id;
    private String level;  // INFO, WARN, ERROR
    private String message;
    private String userId;
    private String entityId;  // Project or Task ID
    private String entityType; // Project, Task
    private LocalDateTime timestamp;
    private Map<String, Object> details;
}

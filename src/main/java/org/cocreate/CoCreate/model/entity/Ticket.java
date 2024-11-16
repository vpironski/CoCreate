package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.TicketStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;

    private String title;
    private String description;

    @Field("status")
    private TicketStatus status;

    @Field("priority")
    private Priority priority;

    @Field("project_id")
    private String projectId;

    @Field("created_by")
    private User createdBy;

    @Field("assigned_to")
    private List<User> assignedTo;

    @Field("source_department")
    private Department sourceDepartment;

    @Field("target_department")
    private Department targetDepartment;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    private List<Map<String, Object>> activityLog;
}
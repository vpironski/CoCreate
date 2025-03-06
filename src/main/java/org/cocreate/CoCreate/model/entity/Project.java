package org.cocreate.CoCreate.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.ProjectStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id = "";
    private String name = "";
    private String description = "";
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Field("tasks")
    private List<Task> tasks = List.of();

    @Field("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startDate = LocalDateTime.now();

    @Field("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate = LocalDateTime.now();

    @Field("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Field("owner")
    private String ownerId = "";

    @Field("team_members")
    private List<User> teamMembers = List.of();

    private Map<String, Object> workflow = Map.of();
    private Map<String, Object> settings = Map.of();

    private Priority priority = Priority.MEDIUM;
    private Integer progress = 0;

    private List<String> tags = List.of();

    @Field("parent_project_id")
    private String parentProjectId = "";

    private Double budget = 0.0;
    private Map<String, Object> resources = Map.of();

    @Field("activity_log")
    private List<Map<String, Object>> activityLog = List.of();

    @Field("custom_fields")
    private Map<String, Object> customFields = Map.of();
}

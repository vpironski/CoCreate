package org.cocreate.CoCreate.model.entity;

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
    private String id;
    private String name;
    private String description;
    private ProjectStatus status;

    @Field("tasks")
    private List<Task> tasks;

    @Field("start_date")
    private LocalDateTime startDate;

    @Field("end_date")
    private LocalDateTime endDate;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("department_id")
    private String departmentId;

    @Field("owner")
    //TODO make class User
    private String owner;

    @Field("team_members")
    //TODO make class User
    private List<String> teamMembers;

    // The "workflow" map defines the stages, task dependencies, and progress, enabling visualizations like Kanban, Scrum, Calendar & etc.
    private Map<String, Object> workflow;
    //Custom prj settings
    private Map<String, Object> settings;

    private Priority priority;
    //Percent
    private Integer progress;
    private List<String> tags;

    @Field("related_tickets")
    //TODO make class Ticket
    private List<String> relatedTickets;

    @Field("parent_project_id")
    private String parentProjectId;

    private Double budget;
    private Map<String, Object> resources;

    @Field("activity_log")
    private List<Map<String, Object>> activityLog;

    @Field("custom_fields")
    private Map<String, Object> customFields;
}
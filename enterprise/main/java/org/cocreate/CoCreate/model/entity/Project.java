package org.cocreate.CoCreate.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomFields;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.ProjectStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    @Field("start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    @Field("end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.now();

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

    private Priority priority = Priority.MEDIUM;

    private List<String> tags = List.of();

    @Field("parent_project_id")
    private String parentProjectId = "";

    @Field("workflow")
    private Workflow workflow = new Workflow();

    @Field("custom_fields")
    private CustomFields customFields = new CustomFields();
}

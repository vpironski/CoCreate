package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.cocreate.CoCreate.model.enums.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String email;

    private String username;

    @Field("password")
    private String password;

    @Field("assigned_tickets")
    private List<String> assignedTickets;

    @Field("company_roles")
    // Roles within the company or department
    private List<String> companyRole;

    @Field("roles")
    private List<UserRole> roles;

    @Field("personal_projects")
    private List<String> personalProjects;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("field_settings")
    private Map<String, String> fieldSettings = Map.of();
}

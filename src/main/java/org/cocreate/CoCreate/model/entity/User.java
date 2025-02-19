package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

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

    @Field("company_id")
    //null if personal
    private String companyId;

    @Field("department")
    //null if personal
    private Department department;

    @Field("assigned_tickets")
    private List<String> assignedTickets;

    @Field("roles")
    // Roles within the company or department
    private List<String> roles;

    @Field("personal_projects")
    private List<String> personalProjects;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;
}

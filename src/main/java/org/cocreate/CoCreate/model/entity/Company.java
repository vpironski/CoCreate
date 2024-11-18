package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@Document (collection = "companies")
public class Company {
    @Id
    private String id;

    private String name;

    private String domain;

    @Field("departments")
    private List<Department> departments;

    @Field("users")
    private List<User> users;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("admin_users")
    private List<User> adminUsers;

    @Field("settings")
    private Map<String, Object> settings; // Custom settings for the company
}

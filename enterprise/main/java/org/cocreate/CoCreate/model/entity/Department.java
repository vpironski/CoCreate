package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class Department {
    private String id;

    private String name;

    private String companyId;

    private List<User> users;
    //ProjectIDs
    private List<String> projects;

    private List<String> incomingTickets;

    private List<String> outgoingTickets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private User departmentHead;

}

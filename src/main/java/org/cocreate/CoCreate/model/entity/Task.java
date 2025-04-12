package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.TaskStatus;

import java.time.LocalDate;

@Setter
@Getter
public class Task {
    private String id;

    private String title;

    private String description;

//    private List<String> assignedUsers;

    private LocalDate startDate;

    private LocalDate endDate;

    private Priority priority;

    private TaskStatus status;

}

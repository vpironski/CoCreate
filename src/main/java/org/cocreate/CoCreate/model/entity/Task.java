package org.cocreate.CoCreate.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.cocreate.CoCreate.model.enums.Priority;
import org.cocreate.CoCreate.model.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class Task {
    private String id;

    private String title;

    private String description;

    private List<String> assignedUsers;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Priority priority;

    private TaskStatus status;

    private List<String> dependencies;

}

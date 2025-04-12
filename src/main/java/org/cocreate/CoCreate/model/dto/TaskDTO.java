package org.cocreate.CoCreate.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.cocreate.CoCreate.model.enums.Priority;

import java.time.LocalDate;

@Getter
@Setter
public class TaskDTO {
    private String title = "";

    private String description = "";

    private String card = "";

//    private List<String> userIds = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.now();

    private Priority priority = Priority.MEDIUM;

}

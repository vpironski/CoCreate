package org.cocreate.CoCreate.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomFields;
import org.cocreate.CoCreate.model.enums.Priority;

import java.time.LocalDate;


@Setter
@Getter
public class ProjectDTO {

    private String name = "";
    private String description = "";

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate = LocalDate.now();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate = LocalDate.now();

    private String parentProjectId = "";

    private CustomFields customFields = new CustomFields();

    private Priority priority = Priority.MEDIUM;
}


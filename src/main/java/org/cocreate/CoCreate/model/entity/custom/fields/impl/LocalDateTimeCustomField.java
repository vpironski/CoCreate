package org.cocreate.CoCreate.model.entity.custom.fields.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomField;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocalDateTimeCustomField implements CustomField<LocalDateTime> {
    private String name;
    private LocalDateTime value;
}

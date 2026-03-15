package org.cocreate.CoCreate.model.entity.custom.fields.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomField;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoubleCustomField implements CustomField<Double> {
    private String name;
    private Double value;
}
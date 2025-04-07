package org.cocreate.CoCreate.model.entity.custom.fields;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class CustomFields {
    private final List<CustomField> customFields = new ArrayList<>();

    public void add(CustomField customField){
        this.customFields.add(customField);
    }
}

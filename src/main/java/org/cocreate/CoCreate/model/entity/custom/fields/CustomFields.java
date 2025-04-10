package org.cocreate.CoCreate.model.entity.custom.fields;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.cocreate.CoCreate.config.serialization.CustomFieldsDeserializer;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor
public class CustomFields {

    @JsonDeserialize(contentUsing = CustomFieldsDeserializer.class)
    private List<CustomField<?>> customFields = new ArrayList<>();

    public void add(CustomField customField){
        this.customFields.add(customField);
    }
}

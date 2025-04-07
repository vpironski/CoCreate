package org.cocreate.CoCreate.model.entity.custom.fields;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StringCustomField implements CustomField<String>{
    private String name;
    private String value;
}

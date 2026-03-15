package org.cocreate.CoCreate.model.entity.custom.fields;

public interface CustomField<T> {
    String getName();
    void setName(String name);

    T getValue();
    void setValue(T value);
}

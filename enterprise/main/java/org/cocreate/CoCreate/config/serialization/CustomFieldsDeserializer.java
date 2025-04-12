package org.cocreate.CoCreate.config.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cocreate.CoCreate.model.entity.custom.fields.CustomField;
import org.cocreate.CoCreate.model.entity.custom.fields.impl.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomFieldsDeserializer extends JsonDeserializer<CustomField<?>> {


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public CustomField<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);

        String name = node.get("name").asText();
        JsonNode valueNode = node.get("value");

        if (valueNode.isBoolean()) {
            // Use your specific BooleanCustomField class
            return new BooleanCustomField(name, valueNode.asBoolean());
        } else if (valueNode.isNumber()) {
            // Differentiate between Integer and Double
            if (valueNode.isIntegralNumber()) {
                // Use your specific IntegerCustomField class
                return new IntegerCustomField(name, valueNode.asInt());
            } else {
                // Use your specific DoubleCustomField class
                return new DoubleCustomField(name, valueNode.asDouble());
            }
        } else if (valueNode.isTextual()) {
            String textValue = valueNode.asText();
            // Try parsing specific formats first
            try {
                LocalDateTime dateTime = LocalDateTime.parse(textValue, DATETIME_FORMATTER);
                // Use your specific LocalDateTimeCustomField class
                return new LocalDateTimeCustomField(name, dateTime);
            } catch (DateTimeParseException e1) {
                try {
                    LocalDate date = LocalDate.parse(textValue, DATE_FORMATTER);
                    // Use your specific LocalDateCustomField class
                    return new LocalDateCustomField(name, date);
                } catch (DateTimeParseException e2) {
                    // If it's not a recognized date/datetime format, treat as String
                    // Use your specific StringCustomField class
                    return new StringCustomField(name, textValue);
                }
            }
        } else if (valueNode.isNull()) {
            // Decide how to handle null. Using StringCustomField with null or empty string?
            // Let's assume null value means StringCustomField based on "team_lead": "" example
            // Use your specific StringCustomField class
            return new StringCustomField(name, null); // Or "" if you prefer
        } else {
            // Handle other unexpected types if necessary
            throw new IOException("Unsupported value type for CustomField: " + valueNode.getNodeType() + " for name: " + name);
        }
    }
}
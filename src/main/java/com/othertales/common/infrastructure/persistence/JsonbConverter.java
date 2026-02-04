package com.othertales.common.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * JPA AttributeConverter for PostgreSQL JSONB columns.
 * Converts between Java Map and JSON string for database storage.
 */
@Converter
public class JsonbConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger log = LoggerFactory.getLogger(JsonbConverter.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final TypeReference<Map<String, Object>> TYPE_REF = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert Map to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(dbData, TYPE_REF);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert JSON to Map: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}

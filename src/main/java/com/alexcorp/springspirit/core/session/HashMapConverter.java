package com.alexcorp.springspirit.core.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {
        try {
            return mapper.writeValueAsString(customerInfo);
        } catch (JsonProcessingException e) {
            log.error("JSON writing error", e);
            return "";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {
        try {
            return mapper.readValue(customerInfoJSON, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("JSON reading error", e);
            return new HashMap<>();
        }
    }
}

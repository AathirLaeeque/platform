package com.leucine.streem.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collection;

public final class JsonUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private JsonUtils() {
  }

  public static <T> T jsonToCollectionType(String data, Class<? extends Collection> collection, Class<?> type)
    throws IOException {
    return (T) objectMapper.readValue(data, objectMapper.getTypeFactory().constructCollectionType(collection, type));
  }

  public static <T> T jsonToCollectionType(Object data, Class<? extends Collection> collection, Class<?> type)
    throws IOException {
    return (T) objectMapper.readValue(objectMapper.writeValueAsString(data), objectMapper.getTypeFactory().constructCollectionType(collection, type));
  }


  public static JsonNode valueToNode(Object value) {
    return objectMapper.valueToTree(value);
  }

  public static JsonNode valueToNode(String content) throws JsonProcessingException {
    return objectMapper.readTree(content);
  }

  public static JsonNode createObjectNode() {
    return objectMapper.createObjectNode();
  }

  public static <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
    return objectMapper.readValue(content, valueType);
  }

  public static <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException {
    return objectMapper.readValue(content, valueTypeRef);
  }

  public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) throws IllegalArgumentException {
    return objectMapper.convertValue(fromValue, toValueTypeRef);
  }

  public static String writeValueAsString(Object value) throws JsonProcessingException {
    return objectMapper.writeValueAsString(value);
  }
}

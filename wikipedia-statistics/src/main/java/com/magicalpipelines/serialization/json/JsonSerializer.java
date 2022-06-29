package com.magicalpipelines.serialization.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerializer<T> implements Serializer<T> {
  private Gson gson =
      new GsonBuilder()
          .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
          .enableComplexMapKeySerialization()
          .setLenient()
          .create();

  /** Default constructor needed by Kafka */
  public JsonSerializer() {}

  @Override
  public void configure(Map<String, ?> props, boolean isKey) {}

  @Override
  public byte[] serialize(String topic, T type) {
    System.out.println(gson.toJson(type));
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      System.out.println(
          "HOLA"
              + Arrays.toString(
                  objectMapper.writeValueAsString(type).getBytes(StandardCharsets.UTF_8)));
      return objectMapper.writeValueAsString(type).getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
    }
    return gson.toJson(type).getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public void close() {}
}

package com.magicalpipelines.serialization.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;

public class JsonDeserializer2 extends JsonDeserializer<Object> implements ContextualDeserializer {
  private Class<?> contentType;

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
      throws JsonMappingException {
    final JavaType wrapperType;

    if (property == null) {
      wrapperType = ctxt.getContextualType();
    } else {
      wrapperType = property.getType();
    }

    final JavaType contentType = wrapperType.containedType(0);
    final JsonDeserializer2 deserializer = new JsonDeserializer2();
    deserializer.contentType = contentType.getRawClass();
    return deserializer;
  }

  @Override
  public Object deserialize(final JsonParser jp, final DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    // Retrieve the object mapper and read the tree.
    ObjectMapper mapper = (ObjectMapper) jp.getCodec();
    JsonNode root = mapper.readTree(jp);
    return mapper.treeToValue(root, this.contentType);
  }
}

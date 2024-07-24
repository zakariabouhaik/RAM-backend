package com.example.rambackend.entities;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.*;

public class ReponseListDeserializer extends JsonDeserializer<List<RegleReponse>> {
    @Override
    public List<RegleReponse> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        List<RegleReponse> result = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode element : node) {
                Iterator<Map.Entry<String, JsonNode>> fields = element.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    RegleReponse regleReponse = new RegleReponse();
                    Regle regle = new Regle();
                    regle.setId(entry.getKey());
                    regleReponse.setRegle(regle);
                    regleReponse.setValue(entry.getValue().asBoolean());
                    result.add(regleReponse);
                }
            }
        }

        return result;
    }
}
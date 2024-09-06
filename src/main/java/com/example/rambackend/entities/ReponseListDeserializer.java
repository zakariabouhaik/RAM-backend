package com.example.rambackend.entities;

import com.example.rambackend.enums.ReponseType;
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

                    JsonNode valueNode = entry.getValue();
                    ReponseType reponseType = ReponseType.valueOf(valueNode.get("value").asText());
                    regleReponse.setValue(reponseType);

                    if (valueNode.has("nonConformeLevel")) {
                        regleReponse.setNonConformeLevel(valueNode.get("nonConformeLevel").asInt());
                    }

                    if (valueNode.has("commentaire")) {
                        regleReponse.setCommentaire(valueNode.get("commentaire").asText());
                    }

                    result.add(regleReponse);
                }
            }
        }

        return result;
    }

    private ReponseType parseReponseType(String value) {
        switch (value.toLowerCase()) {
            case "true":
            case "conforme":
                return ReponseType.CONFORME;
            case "false":
            case "non_conforme":
                return ReponseType.NON_CONFORME;
            case "observation":
                return ReponseType.OBSERVATION;
            case "amelioration":
                return ReponseType.AMELIORATION;
            default:
                throw new IllegalArgumentException("Invalid response type: " + value);
        }
    }
}
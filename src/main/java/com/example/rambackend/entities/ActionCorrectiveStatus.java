package com.example.rambackend.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
@Data
@Document

public class ActionCorrectiveStatus {
    @Id
    private String id;
    private String userId;
    private String auditId;
    private Map<String, Boolean> actionsState;
    private boolean sent;
}

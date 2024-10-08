package com.example.rambackend.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class ActionCorrective {
    @Id
    private String id;
    private String auditId;
    private List<String> descriptions;
}


package com.example.rambackend.entities;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class RapportAdmin {

    @Id
    private String id;
    private String nom;
    private byte[] contenu;


}

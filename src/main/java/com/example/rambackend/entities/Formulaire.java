package com.example.rambackend.entities;


import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document
public class Formulaire {

    @Id
    private String id;

    private String nom;

    @Field
    List<Section>sectionList;


}

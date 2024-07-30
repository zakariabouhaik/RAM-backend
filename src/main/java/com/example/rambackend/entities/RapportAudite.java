package com.example.rambackend.entities;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "rapportAudite")
public class RapportAudite {

        @Id
        private String id;

        private String nom;

        private byte[] contenu;



}

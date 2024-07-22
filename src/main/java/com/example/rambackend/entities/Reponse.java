package com.example.rambackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document
public class Reponse {
    @Id

    private String id;
    private boolean reps;

    /*
    @ManyToOne
    private Audit audit;
    @ManyToOne
    private Regle regle;
     */

}

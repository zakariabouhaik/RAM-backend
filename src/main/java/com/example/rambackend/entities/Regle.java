package com.example.rambackend.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document
public class Regle {

        @Id
        private String id;
        private String description;

        /*
        @OneToMany(mappedBy = "regle", fetch = FetchType.LAZY)
        private List<Reponse> reponseList  = new ArrayList<>();
         */
}

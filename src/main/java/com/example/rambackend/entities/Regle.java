package com.example.rambackend.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Regle {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String description;

        /*
        @OneToMany(mappedBy = "regle", fetch = FetchType.LAZY)
        private List<Reponse> reponseList  = new ArrayList<>();
         */
}

package com.example.rambackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Data
@Document
public class Audit {
    @Id
    private String id;
    private String escaleVille;
    private LocalDate dateProgramme;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean archivee;
    private String nomFormulaire;
    private String rapportAudit;
    private String rapportAction;

    /*
    @OneToMany(mappedBy = "audit", fetch = FetchType.LAZY)
    private List<Reponse> reponseList  = new ArrayList<>();
     */

}

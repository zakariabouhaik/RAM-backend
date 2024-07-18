package com.example.rambackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String escaleVille;
    private LocalDate dateProgramme;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean archivee;
    private String nomFormulaire;
    private String rapportAudit;
    private String rapportAction;

}

package com.example.rambackend.entities;

import com.example.rambackend.enums.EtatAudit;
import com.example.rambackend.enums.TypeAudit;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Audit {
    @Id
    private String id;
    private String escaleVille;
    private String aeroport;
    private LocalDate dateProgramme;
    private LocalDate dateDebut;
    private LocalDate observationsurplacedate ;
    private LocalDate dateFin;
    private boolean archivee;

    private Formulaire formulaire;
    private Utilisateur auditeur;
    private Long numeroOrdre;
    private Utilisateur audite;
    private String handlingProvider;
    private RapportAdmin rapportAdmin;
    private String rapportAction;
    @Enumerated(EnumType.STRING)
    private EtatAudit etatAudit = EtatAudit.PROGRAMME;
    @Enumerated(EnumType.STRING)
    private TypeAudit typeAudit;
    private Generalities generalities;
    private List<PersonneRencontrees> personneRencontresees = new ArrayList<>();
    private boolean isGeneralitiesSent= false;
    private boolean auditeregistred= false;

}


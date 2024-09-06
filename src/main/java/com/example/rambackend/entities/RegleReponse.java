package com.example.rambackend.entities;

import com.example.rambackend.enums.ReponseType;
import lombok.Data;

@Data
public class RegleReponse {
    private Regle regle;
    private ReponseType value;
    private Integer nonConformeLevel;
    private String commentaire;
}

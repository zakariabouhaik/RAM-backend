package com.example.rambackend.services;

import com.example.rambackend.entities.Formulaire;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FormulaireService {

    Formulaire createFormulaire(Formulaire formulaire);
    Formulaire getFormulaire(String id);
    List<Formulaire>getAllFormulaires();
    Formulaire updateFormulaire(String id, Formulaire formulaire);
    void deleteFormulaire(String id);

}

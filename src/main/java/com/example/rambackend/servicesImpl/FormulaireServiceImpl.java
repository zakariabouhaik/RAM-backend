package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Formulaire;
import com.example.rambackend.entities.Section;
import com.example.rambackend.repository.FormulaireRepository;
import com.example.rambackend.repository.SectionRepository;
import com.example.rambackend.services.FormulaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FormulaireServiceImpl implements FormulaireService {

    @Autowired
    FormulaireRepository formulaireRepository;
    @Autowired
    SectionRepository sectionRepository;

    @Override
    public Formulaire createFormulaire(Formulaire formulaire) {
    List<Section>compleSections = new ArrayList<>();

    for(Section section : formulaire.getSectionList()){
        Section completeSection = sectionRepository.findById(section.getId()).orElse(null);
        compleSections.add(completeSection);
    }

    formulaire.setSectionList(compleSections);

        return formulaireRepository.save(formulaire);
    }

    @Override
    public Formulaire getFormulaire(String id) {
        return formulaireRepository.findById(id).orElse(null);
    }

    @Override
    public List<Formulaire> getAllFormulaires() {
        return formulaireRepository.findAll();
    }

    @Override
    public Formulaire updateFormulaire(String id, Formulaire formulaire) {
        Formulaire f = formulaireRepository.findById(id).orElse(null);
        f.setSectionList(formulaire.getSectionList());
        return formulaireRepository.save(f);
    }

    @Override
    public void deleteFormulaire(String id) {
    formulaireRepository.deleteById(id);
    }
}

package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.entities.Formulaire;
import com.example.rambackend.entities.Regle;
import com.example.rambackend.entities.Section;
import com.example.rambackend.repository.FormulaireRepository;
import com.example.rambackend.repository.RegleRepository;
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
    @Autowired
    RegleRepository regleRepository;

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
    public Formulaire updateFormulaire(String id, Formulaire updatedFormulaire) {
        Formulaire existingFormulaire = formulaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulaire not found"));

        existingFormulaire.setNom(updatedFormulaire.getNom());

        List<Section> updatedSections = new ArrayList<>();
        for (Section updatedSection : updatedFormulaire.getSectionList()) {
            Section section;
            if (updatedSection.getId() != null) {
                section = sectionRepository.findById(updatedSection.getId())
                        .orElseThrow(() -> new RuntimeException("Section not found"));
                section.setDescription(updatedSection.getDescription());
            } else {
                section = new Section();
                section.setDescription(updatedSection.getDescription());
            }

            List<Regle> updatedRegles = new ArrayList<>();
            for (Regle updatedRegle : updatedSection.getRegles()) {
                Regle regle;
                if (updatedRegle.getId() != null) {
                    regle = regleRepository.findById(updatedRegle.getId())
                            .orElseThrow(() -> new RuntimeException("Regle not found"));
                    regle.setDescription(updatedRegle.getDescription());
                    regle.setActionCorrective(updatedRegle.getActionCorrective()); // Now just a string
                } else {
                    regle = new Regle();
                    regle.setDescription(updatedRegle.getDescription());
                    regle.setActionCorrective(updatedRegle.getActionCorrective()); // Now just a string
                }
                regle = regleRepository.save(regle);
                updatedRegles.add(regle);
            }

            section.setRegles(updatedRegles);
            section = sectionRepository.save(section);
            updatedSections.add(section);
        }

        existingFormulaire.setSectionList(updatedSections);
        return formulaireRepository.save(existingFormulaire);
    }



    @Override
    public void deleteFormulaire(String id) {
    formulaireRepository.deleteById(id);
    }
}

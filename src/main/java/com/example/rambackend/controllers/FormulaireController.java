package com.example.rambackend.controllers;

import com.example.rambackend.entities.Formulaire;
import com.example.rambackend.services.FormulaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Formulaire")
public class FormulaireController {
    @Autowired
    FormulaireService formulaireService;

    @PostMapping
    private Formulaire createFormulaire(@RequestBody Formulaire formulaire){
        return formulaireService.createFormulaire(formulaire);
    }

    @GetMapping
    private List<Formulaire> getAllFormulaire(){
        return formulaireService.getAllFormulaires();
    }

    @DeleteMapping("/{id}")
    private void DeleteFormulaire(@PathVariable String id){
        formulaireService.deleteFormulaire(id);
    }

    @GetMapping("/{id}")
    private Formulaire getFormulaire(@PathVariable String id){
        return formulaireService.getFormulaire(id);
    }

    @PutMapping("/{id}")
    private Formulaire updateFormulaire(@PathVariable String id,@RequestBody Formulaire formulaire){

        return formulaireService.updateFormulaire(id, formulaire);
    }

}

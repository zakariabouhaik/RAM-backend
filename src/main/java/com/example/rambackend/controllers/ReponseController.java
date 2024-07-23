package com.example.rambackend.controllers;
import com.example.rambackend.entities.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.rambackend.services.ReponseService;

import java.util.List;

@RestController
@RequestMapping("/Reponse")
public class ReponseController {
    @Autowired
    private ReponseService reponseService;

    @PostMapping
    public Reponse addReponse(@RequestBody Reponse reponse) {
        return reponseService.saveReponse(reponse);
    }

    @GetMapping
    public List<Reponse> getAllReponses() {
        return reponseService.getAllReponses();
    }

    @GetMapping("/{id}")
    public Reponse getReponse(@PathVariable String id) {
        return reponseService.getReponseById(id);
    }
    @DeleteMapping("/{id}")
    public void deleteReponse(@PathVariable String id) {
        reponseService.deleteReponseById(id);
    }

    @PutMapping("/{id}")
    public Reponse editReponse(@PathVariable String id, @RequestBody Reponse reponse) {
        return reponseService.updateReponse(id, reponse);
    }

}

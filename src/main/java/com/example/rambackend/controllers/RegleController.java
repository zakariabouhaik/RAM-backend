package com.example.rambackend.controllers;

import com.example.rambackend.entities.Regle;
import com.example.rambackend.services.RegleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Regle")
public class RegleController {
    @Autowired
    private RegleService regleService;

    @PostMapping
    public Regle addRegle(@RequestBody Regle regle) {
        return regleService.saveRegle(regle);
    }

    @GetMapping
    public List<Regle> getAllRegles() {
        return regleService.getAllRegles();
    }

    @GetMapping("/{id}")
    public Regle getRegle(@PathVariable Long id) {
        return regleService.getRegleById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRegle(@PathVariable Long id) {
        regleService.deleteRegleById(id);
    }

    @PutMapping("/{id}")
    public Regle editRegle(@PathVariable Long id, @RequestBody Regle regle) {
        return regleService.updateRegle(id, regle);
    }
}

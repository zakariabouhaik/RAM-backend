package com.example.rambackend.controllers;

import com.example.rambackend.entities.RapportAdmin;
import com.example.rambackend.services.NotificationService;
import com.example.rambackend.services.RapportAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/RapportAdmin")
public class RapportAdminController {

    @Autowired
    RapportAdminService rapportAdminService;

    @PostMapping
    private RapportAdmin addRapportAdmin(@RequestBody RapportAdmin notification){
        return rapportAdminService.saverapportAdmin(notification);
    }

    @GetMapping("/{id}")
    private RapportAdmin getRapportAdmin(@PathVariable String id){
        return rapportAdminService.getrapportAdminById(id);
    }

    @GetMapping()
    private List<RapportAdmin> getallRapportAdmins(){
        return rapportAdminService.getAllrapportAdmins();
    }
    @DeleteMapping("/{id}")
    public void deleteRapportAdmin(@PathVariable String id){
        rapportAdminService.deleterapportAdminById(id);
    }

}


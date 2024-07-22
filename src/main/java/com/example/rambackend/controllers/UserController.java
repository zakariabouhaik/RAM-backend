package com.example.rambackend.controllers;

import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping
    private Utilisateur addUser(@RequestBody Utilisateur user){
        return userService.createUtilisateur(user);
    }

    @GetMapping("/{id}")
    private Utilisateur getUser(@PathVariable String id){
        return userService.getUtilisateurById(id);
    }

    @GetMapping()
    private List<Utilisateur>getallUsers(){
        return userService.getAllUtilisateurs();
    }

    @PutMapping("/{id}")
    public Utilisateur EditUser (@PathVariable String id, @RequestBody Utilisateur user){
        return userService.updateUtilisateur(id,user);
    }


    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id){
        userService.deleteUtilisateur(id);
    }

}

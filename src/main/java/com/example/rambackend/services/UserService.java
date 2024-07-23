package com.example.rambackend.services;

import com.example.rambackend.entities.Utilisateur;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    Utilisateur createUtilisateur(Utilisateur user);
    Utilisateur getUtilisateurById(String id);
    List<Utilisateur> getAllUtilisateurs();
    Utilisateur updateUtilisateur(String id, Utilisateur utilisateur);
    void deleteUtilisateur(String id);
}

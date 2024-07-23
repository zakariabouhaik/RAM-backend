package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.repository.UserRepository;
import com.example.rambackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Utilisateur createUtilisateur(Utilisateur user) {
        return userRepository.save(user);
    }

    @Override
    public Utilisateur getUtilisateurById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return userRepository.findAll();
    }

    @Override
    public Utilisateur updateUtilisateur(String id, Utilisateur utilisateurDetails) {
        return userRepository.findById(id)
                .map(utilisateur -> {
                    if (utilisateurDetails.getNom_complet() != null) {
                        utilisateur.setNom_complet(utilisateurDetails.getNom_complet());
                    }
                    if (utilisateurDetails.getEmail() != null) {
                        utilisateur.setEmail(utilisateurDetails.getEmail());
                    }
                    if (utilisateurDetails.getMdp() != null) {
                        utilisateur.setMdp(utilisateurDetails.getMdp());
                    }
                    if (utilisateurDetails.getTel() != null) {
                        utilisateur.setTel(utilisateurDetails.getTel());
                    }
                    if (utilisateurDetails.getRole() != null) {
                        utilisateur.setRole(utilisateurDetails.getRole());
                    }


                    return userRepository.save(utilisateur);
                })
                .orElse(null);
    }

    @Override
    public void deleteUtilisateur(String id) {
        userRepository.deleteById(id);
    }
}

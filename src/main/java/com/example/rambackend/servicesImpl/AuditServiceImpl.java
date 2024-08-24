package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Formulaire;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.repository.FormulaireRepository;
import com.example.rambackend.repository.UserRepository;
import com.example.rambackend.services.AuditService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class AuditServiceImpl implements AuditService {
    @Autowired
    private  AuditRepository auditRepository;
    @Autowired
    private FormulaireRepository formulaireRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeycloakServiceImpl keycloakService;


    @Override
    public Audit saveAudit(Audit audit) {
        Formulaire formulaire = formulaireRepository.findById(audit.getFormulaire().getId())
                .orElseThrow(() -> new EntityNotFoundException("Formulaire not found"));

        return keycloakService.getUserById(audit.getAuditeur().getId())
                .map(utilisateur -> {
                    audit.setFormulaire(formulaire);
                    audit.setAuditeur(utilisateur);
                    return auditRepository.save(audit);
                })
                .block(); // Bloque pour obtenir le résultat, considérez d'utiliser Mono/Flux si possible
    }
    @Override
    public List<Audit> getAllAudits() {
        List<Audit> audits = auditRepository.findAll();
        return audits.stream()
                .filter(audit -> audit.getAuditeur() != null)
                .collect(Collectors.toList());
    }
    @Override
    public Audit getAuditById(String id) {
        return auditRepository.findById(id).get();
    }

    @Override
    public void deleteAuditById(String id) {
        auditRepository.deleteById(id);
    }

    @Override
    public Audit updateAudit(String id, Audit audit) {
        Audit currAudit = getAuditById(id);


        if (audit.getEscaleVille() != null) currAudit.setEscaleVille(audit.getEscaleVille());
        if (audit.getDateProgramme() != null) currAudit.setDateProgramme(audit.getDateProgramme());
        if (audit.getDateDebut() != null) currAudit.setDateDebut(audit.getDateDebut());
        if (audit.getDateFin() != null) currAudit.setDateFin(audit.getDateFin());
        currAudit.setArchivee(audit.isArchivee());
        if (audit.getRapportAdmin() != null) currAudit.setRapportAdmin(audit.getRapportAdmin());
        if (audit.getRapportAction() != null) currAudit.setRapportAction(audit.getRapportAction());


        if (audit.getFormulaire() != null && !audit.getFormulaire().getId().equals(currAudit.getFormulaire().getId())) {
            Formulaire formulaire = formulaireRepository.findById(audit.getFormulaire().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Formulaire not found"));
            currAudit.setFormulaire(formulaire);
        }


        if (audit.getAuditeur() != null && !audit.getAuditeur().getId().equals(currAudit.getAuditeur().getId())) {
            return keycloakService.getUserById(audit.getAuditeur().getId())
                    .map(utilisateur -> {
                        currAudit.setAuditeur(utilisateur);
                        return auditRepository.save(currAudit);
                    })
                    .block();
        } else {
            return auditRepository.save(currAudit);
        }
    }


    public List<Audit> findAuditsByUserId(String userId) {
        return auditRepository.findByAuditeurId(userId);
    }
    public List<Audit> findAuditsByAuditeId(String auditeId) {
        return auditRepository.findByAuditeId(auditeId);
    }
}

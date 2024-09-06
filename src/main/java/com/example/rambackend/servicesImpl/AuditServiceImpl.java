package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.*;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.repository.FormulaireRepository;
import com.example.rambackend.repository.UserRepository;
import com.example.rambackend.services.AuditService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Override
    public Audit saveAudit(Audit audit) {
        Formulaire formulaire = formulaireRepository.findById(audit.getFormulaire().getId())
                .orElseThrow(() -> new EntityNotFoundException("Formulaire not found"));

        return keycloakService.getUserById(audit.getAuditeur().getId())
                .flatMap(utilisateur -> {
                    audit.setFormulaire(formulaire);
                    audit.setAuditeur(utilisateur);
                    Long lastOrderNumber = auditRepository.findTopByOrderByNumeroOrdreDesc()
                            .map(Audit::getNumeroOrdre)
                            .orElse(0L);

                    audit.setNumeroOrdre(lastOrderNumber + 1);
                    Audit savedAudit = auditRepository.save(audit);

                    String message = "Vous avez été choisi pour effectuer un audit à " + audit.getEscaleVille() + " du " + audit.getDateDebut() + " au " + audit.getDateFin();

                    return keycloakService.addNotificationToUser(utilisateur.getIdMongo(), utilisateur.getId(), message)
                            .thenReturn(savedAudit);

                })
                .block();
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

    @Override
    public Audit saveGeneralitie(String id, Generalities generalities) {
        Audit audit= getAuditById(id);
        audit.setGeneralities(generalities);
        return auditRepository.save(audit);
    }

    @Override
    public Audit sendGeneralities(String id) {
        Audit audit = getAuditById(id);
        if(audit.getGeneralities()==null){
            throw new IllegalStateException("Generalities must be saved before sending");
        }
        audit.setGeneralitiesSent(true);
        return auditRepository.save(audit);
    }

    @Override
    public Audit savePersonnesRencontrees(String id, List<PersonneRencontrees> personnes) {
        logger.info("Saving personnes rencontrees for audit id: {}", id);
        logger.info("Personnes to save: {}", personnes);

        Audit audit = getAuditById(id);
        if (audit.getPersonneRencontresees() == null) {
            audit.setPersonneRencontresees(new ArrayList<>());
        }
        audit.getPersonneRencontresees().addAll(personnes);

        Audit savedAudit = auditRepository.save(audit);
        logger.info("Saved audit with updated personnes rencontrees: {}", savedAudit);

        return savedAudit;
    }

}

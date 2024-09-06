package com.example.rambackend.controllers;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Generalities;
import com.example.rambackend.entities.PersonneRencontrees;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.enums.UserRole;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.services.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Audit")

public class AuditController {
    @Autowired
    private AuditService auditService;
    @Autowired
    private AuditRepository auditRepository;


    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    @PostMapping
    public Audit addAudit(@RequestBody Audit audit) {
        System.out.println("Received audit: " + audit);
        return auditService.saveAudit(audit);
    }

    @GetMapping()
    private List<Audit> getAllAudits(){
        return auditService.getAllAudits();
    }

    @GetMapping("/{id}")
    public Audit getAudit(@PathVariable String id) {
        return auditService.getAuditById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAudit(@PathVariable String id) {
        auditService.deleteAuditById(id);
    }

    @PutMapping("/{id}")
    public Audit editAudit(@PathVariable String id, @RequestBody Audit audit) {
        return auditService.updateAudit(id, audit);
    }




    @GetMapping("/user/{userId}")
    public List<Audit> getAuditsByUserId(@PathVariable String userId) {
        System.out.println("Fetching audits for userId: " + userId);
        List<Audit> audits = auditService.findAuditsByUserId(userId);
        System.out.println("Audits found: " + audits);
        return audits;
    }

    @GetMapping("/audite/{userId}")
    public List<Audit> getAuditsByAuditeId(@PathVariable String userId) {
        System.out.println("Fetching audits for auditeId: " + userId);
        List<Audit> audits = auditService.findAuditsByAuditeId(userId);
        System.out.println("Audits found: " + audits);
        return audits;
    }

    @PutMapping("/{id}/generalities")
    public Audit saveGeneralities(@PathVariable String id, @RequestBody Generalities generalities){
        return auditService.saveGeneralitie(id,generalities);
    }

    @PutMapping("/{id}/send-generalities")
    public Audit sendGeneralities(@PathVariable String id) {
        return auditService.sendGeneralities(id);
    }
    @GetMapping("/{id}/generalities")
    public ResponseEntity<Map<String, Object>> getGeneralities(@PathVariable String id) {
        Audit audit = auditService.getAuditById(id);
        if (audit != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("generalities", audit.getGeneralities());
            response.put("isGeneralitiesSent", audit.isGeneralitiesSent());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @PutMapping("/{id}/personnes-rencontrees")
    public ResponseEntity<Audit> savePersonnesRencontrees(@PathVariable String id, @RequestBody List<PersonneRencontrees> personnes) {
        logger.info("Received request to save personnes rencontrees for audit id: {}", id);
        logger.info("Raw received data: {}", personnes);

        if (personnes == null || personnes.isEmpty()) {
            logger.warn("Received null or empty list of personnes");
            return ResponseEntity.badRequest().build();
        }

        List<PersonneRencontrees> personnesNonVides = personnes.stream()
                .filter(p -> p != null && p.getFullname() != null && !p.getFullname().isEmpty()
                        && p.getTitle() != null && !p.getTitle().isEmpty())
                .collect(Collectors.toList());

        logger.info("Filtered non-empty personnes: {}", personnesNonVides);

        if (personnesNonVides.isEmpty()) {
            logger.warn("No valid personnes to save");
            return ResponseEntity.badRequest().build();
        }

        Audit audit = auditService.savePersonnesRencontrees(id, personnesNonVides);
        logger.info("Saved personnes rencontrees. Updated audit: {}", audit);

        return ResponseEntity.ok(audit);
    }

    @GetMapping("/{id}/personnes-rencontrees")
    public ResponseEntity<List<PersonneRencontrees>> getPersonnesRencontrees(@PathVariable String id) {
        Audit audit = auditService.getAuditById(id);
        if (audit != null && audit.getPersonneRencontresees() != null) {
            return ResponseEntity.ok(audit.getPersonneRencontresees());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{auditId}/audite")
    public ResponseEntity<Audit> updateAudite(@PathVariable String auditId, @RequestBody Utilisateur auditeDTO,  @RequestParam LocalDate localDate) {
        Audit audit = auditService.getAuditById(auditId);
        if (audit == null) {
            return ResponseEntity.notFound().build();
        }


        audit.setObservationsurplacedate(localDate);

        // Mise à jour de l'audité existant au lieu d'en créer un nouveau
        Utilisateur audite = audit.getAudite();
        if (audite == null) {
            audite = new Utilisateur();
            audit.setAudite(audite);
        }

        // Mise à jour des champs de l'audité

        audite.setEmploi(auditeDTO.getEmploi());
        audite.setPhonenumber(auditeDTO.getPhonenumber());


        audit.setAuditeregistred(true);

        Audit updatedAudit = auditRepository.save(audit);

        return ResponseEntity.ok(updatedAudit);
    }
}

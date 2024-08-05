package com.example.rambackend.controllers;
import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.RapportAdmin;
import com.example.rambackend.entities.Reponse;
import com.example.rambackend.services.AuditService;
import com.example.rambackend.services.RapportAdminService;
import com.example.rambackend.servicesImpl.EmailService;
import com.example.rambackend.servicesImpl.PdfService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.rambackend.services.ReponseService;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Reponse")
public class ReponseController {
    @Autowired
    private ReponseService reponseService;

    @Autowired
    private PdfService pdfService;
    @Autowired
    private AuditService auditService;
    @Autowired
    private RapportAdminService rapportAdminService;
    @Autowired
    private EmailService emailService;


    @PostMapping
    public ResponseEntity<?> addReponse(@RequestBody Reponse reponse) {
        try {
            Reponse savedReponse = reponseService.saveReponse(reponse);
            Audit audit = auditService.getAuditById(savedReponse.getAudit().getId());

            byte[] pdfBytes = pdfService.generatePdf(audit, savedReponse.getReponses());

            if (pdfBytes != null) {
                String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

                // Envoyer l'email avec le PDF en pièce jointe
                String to = "zakariayoza123@gmail.com";
                String subject = "Rapport d'audit - " + audit.getEscaleVille();
                String text = "Veuillez trouver ci-joint le rapport d'audit pour " + audit.getEscaleVille() + ".";
                String attachmentName = "rapport_audit_" + audit.getId() + ".pdf";

                emailService.sendEmailWithAttachment(to, subject, text, pdfBytes, attachmentName);

                Map<String, Object> response = new HashMap<>();
                response.put("audit", audit);
                response.put("pdfContent", pdfBase64);
                response.put("message", "Email envoyé avec succès à " + to);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la génération du PDF");
            }
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
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

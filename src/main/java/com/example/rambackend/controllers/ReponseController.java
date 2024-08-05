package com.example.rambackend.controllers;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.RapportAudite;
import com.example.rambackend.entities.Reponse;
import com.example.rambackend.repository.ReponseRepository;
import com.example.rambackend.services.AuditService;
import com.example.rambackend.services.ReponseService;
import com.example.rambackend.servicesImpl.EmailService;
import com.example.rambackend.servicesImpl.PdfService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/Reponse")
public class ReponseController {
    @Autowired
    private ReponseService reponseService;
    @Autowired
    private ReponseRepository reponseRepository;
    @Autowired
    private PdfService pdfService;
    @Autowired
    private AuditService auditService;
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

    @PostMapping("/send-pdf-email")
    public ResponseEntity<String> sendPdfEmail(@RequestBody Map<String, String> requestBody) {
        String reponseId = requestBody.get("reponseId");

        Reponse reponse = reponseService.getReponseById(reponseId);
        if (reponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id " + reponseId);
        }

        try {
            Audit audit = reponse.getAudit();
            byte[] pdfBytes = pdfService.generatePdf(audit, reponse.getReponses());
            String filename = "reponse_" + reponseId + ".pdf";

            emailService.sendEmailWithAttachment("dalalaziz16@gmail.com", "Rapport d'audit", "Veuillez trouver ci-joint le rapport d'audit.", pdfBytes, filename);

            return ResponseEntity.ok("Email envoyé");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    @PostMapping("/save-pdf")
    public ResponseEntity<String> savePdf(@RequestBody Map<String, String> request) {
        String rapportId = request.get("rapportId");
        if (rapportId == null || rapportId.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid reponseId");
        }

        try {
            Optional<Reponse> reponseOptional = reponseRepository.findById(rapportId);
            if (!reponseOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id " + rapportId);
            }

            Reponse reponse = reponseOptional.get();
            Audit audit = reponse.getAudit();
            byte[] pdfBytes = pdfService.generatePdf(audit, reponse.getReponses());
            String filename = "reponse_" + rapportId + ".pdf";
            RapportAudite savedRapportAudite = pdfService.savePdfToRapportAudite(pdfBytes, filename);

            return ResponseEntity.ok("PDF saved successfully with ID: " + savedRapportAudite.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving PDF: " + e.getMessage());
        }
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String id) {
        return pdfService.getPdfById(id);
    }
}
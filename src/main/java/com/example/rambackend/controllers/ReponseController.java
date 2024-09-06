package com.example.rambackend.controllers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.RapportAudite;
import com.example.rambackend.entities.Reponse;
import com.example.rambackend.repository.ReponseRepository;
import com.example.rambackend.services.AuditService;
import com.example.rambackend.services.ReponseService;
import com.example.rambackend.servicesImpl.EmailService;
import com.example.rambackend.servicesImpl.KeycloakServiceImpl;
import com.example.rambackend.servicesImpl.PdfService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
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

    @Autowired
    private AmazonS3 s3client;

    @Autowired
    private KeycloakServiceImpl keycloakService;

    private static final String BUCKET_NAME = "ram-rapports";
    private static final String AUDITE_FOLDER = "Audite/";
    private static final String ADMIN_FOLDER = "Admin/";



    @PostMapping


    public ResponseEntity<Reponse> addReponse(@RequestBody Reponse reponse) {
        Reponse savedReponse = reponseService.saveReponse(reponse);
        return ResponseEntity.ok(savedReponse);
    }

    @PostMapping("/save")
    public ResponseEntity<Reponse> saveReponse(@RequestBody Reponse reponse) {
        Reponse savedReponse = reponseService.saveReponse(reponse);
        return ResponseEntity.ok(savedReponse);
    }





    @PostMapping("/send-notifications")
    public Mono<ResponseEntity<Void>> sendNotifications(@RequestBody Map<String, String> requestBody) {
        String reponseId = requestBody.get("reponseId");
        String auditId = requestBody.get("auditId");
        String message = requestBody.get("message");

        return Mono.fromCallable(() -> auditService.getAuditById(auditId))
                .flatMap(audit -> {
                    if (audit == null) {
                        return Mono.just(ResponseEntity.notFound().<Void>build());
                    }

                    String adminId = "66c24a34556fca12b8653199";
                    String auditeurId = audit.getAuditeur().getId();
                    String auditeKeycloakId = audit.getAudite().getId();

                    return keycloakService.getIdMongoByUserId(auditeKeycloakId)
                            .flatMap(auditeMongoId -> {
                                if (auditeMongoId == null) {
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build());
                                }

                                return Mono.when(
                                        keycloakService.addNotificationToUser(adminId, auditeurId, message),
                                        keycloakService.addNotificationToUser(auditeMongoId, auditeurId, message)
                                ).then(Mono.just(ResponseEntity.ok().<Void>build()));
                            });
                })
                .onErrorResume(e -> {
                    // Log the error
                    System.err.println("Error sending notifications: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Void>build());
                });
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
    public ResponseEntity<String> sendPdfEmailAdmin(@RequestBody Map<String, String> requestBody) {
        String reponseId = requestBody.get("reponseId");
        Reponse reponse = reponseService.getReponseById(reponseId);
        if (reponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id " + reponseId);
        }

        try {
            byte[] pdfBytes = pdfService.generatePdf(reponse);
            String filename = "rapport_audit_admin_" + reponse.getAudit().getId() + ".pdf";

            // Sauvegarde dans S3
            String s3Key = ADMIN_FOLDER + filename;
            uploadToS3(pdfBytes, s3Key);

            // Envoi de l'email à l'admin
            String to = "zakariayoza123@example.com";
            String subject = "Rapport d'audit (Admin) - " + reponse.getAudit().getEscaleVille();
            String text = "Veuillez trouver ci-joint le rapport d'audit administratif pour " + reponse.getAudit().getEscaleVille() + ".";

            emailService.sendEmailWithAttachment(to, subject, text, pdfBytes, filename);

            return ResponseEntity.ok("Admin email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending admin email: " + e.getMessage());
        }
    }


    @PutMapping("/finalize/{id}")
    public ResponseEntity<Reponse> finalizeReponse(@PathVariable String id) {
        Reponse reponse = reponseService.getReponseById(id);
        if (reponse == null) {
            return ResponseEntity.notFound().build();
        }
        reponse.setTemporary(false);
        Reponse updatedReponse = reponseService.updateReponse(id,reponse);
        return ResponseEntity.ok(updatedReponse);
    }
    @PostMapping("/save-pdf")
    public ResponseEntity<?> generateAndSavePdf(@RequestBody Map<String, String> requestBody) {
        String reponseId = requestBody.get("rapportId");
        if (reponseId == null || reponseId.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid reponseId");
        }

        try {
            Reponse reponse = reponseService.getReponseById(reponseId);
            if (reponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id " + reponseId);
            }

            // Générer le PDF une seule fois
            byte[] pdfBytes = pdfService.generatePdf(reponse);

            // Nom du fichier
            String filename = "rapport_audit_" + reponse.getAudit().getId() + ".pdf";

            // Sauvegarde dans S3 - dossier Audite
            String auditeS3Key = AUDITE_FOLDER + filename;
            uploadToS3(pdfBytes, auditeS3Key);

            // Sauvegarde dans S3 - dossier Admin
            String adminS3Key = ADMIN_FOLDER + filename;
            uploadToS3(pdfBytes, adminS3Key);

            // Sauvegarde dans la base de données

            // Préparer la réponse HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", filename);

            String to = "zakariayoza123@gmail.com";
            String subject = "Rapport d'audit (Admin) - " + reponse.getAudit().getEscaleVille();
            String text = "Veuillez trouver ci-joint le rapport d'audit administratif pour " + reponse.getAudit().getEscaleVille() + ".";

            emailService.sendEmailWithAttachment(to, subject, text, pdfBytes, filename);


            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating and saving PDF: " + e.getMessage());
        }
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String id) {
        return pdfService.getPdfById(id);
    }





    private void uploadToS3(byte[] content, String key) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentType("application/pdf");

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, new ByteArrayInputStream(content), metadata);
        s3client.putObject(putObjectRequest);
    }


    @GetMapping("/audit/{auditId}")
    public ResponseEntity<Reponse> getReponseByAuditId(@PathVariable String auditId) {
        Reponse reponse = reponseService.getReponseByAuditId(auditId);
        if (reponse == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(reponse);
    }



}
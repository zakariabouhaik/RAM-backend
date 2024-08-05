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
import com.example.rambackend.servicesImpl.PdfService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private static final String BUCKET_NAME = "ram-rapports";
    private static final String AUDITE_FOLDER = "Audite/";
    private static final String ADMIN_FOLDER = "Admin/";



    @PostMapping


    public ResponseEntity<Reponse> addReponse(@RequestBody Reponse reponse) {
        Reponse savedReponse = reponseService.saveReponse(reponse);
        return ResponseEntity.ok(savedReponse);
    }
    @PostMapping("/send-pdf-email2")
    public ResponseEntity<String> sendPdfEmail2(@RequestBody Map<String, String> requestBody) {
        String reponseId = requestBody.get("reponseId");

        Reponse reponse = reponseService.getReponseById(reponseId);
        if (reponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id " + reponseId);
        }

        try {
            Audit audit = reponse.getAudit();
            byte[] pdfBytes = pdfService.generatePdf(audit, reponse.getReponses());
            String filename = "rapport_audit_" + audit.getId() + ".pdf";

            String s3Key = ADMIN_FOLDER+filename;
            uploadToS3(pdfBytes,s3Key);


            String to = "zakariayoza123@gmail.com";
            String subject = "Rapport d'audit - " + audit.getEscaleVille();
            String text = "Veuillez trouver ci-joint le rapport d'audit pour " + audit.getEscaleVille() + ".";

            emailService.sendEmailWithAttachment(to, subject, text, pdfBytes, filename);

            return ResponseEntity.ok("Email envoyé à zakariayoza123@gmail.com");
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
            byte[] pdfBytes = pdfService.generatePdfBytesForReponse(reponse);
            String filename = "reponse_" + reponseId + ".pdf";

            String s3Key = AUDITE_FOLDER + filename;
            uploadToS3(pdfBytes,s3Key);


            emailService.sendEmailWithAttachment("dalalaziz16@gmail.com", "Objet de l'Email", "Contenu de l'Email", pdfBytes, filename);

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



    private void uploadToS3 (byte[] content,String key){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentType("application/pdf");

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,key,new ByteArrayInputStream(content),metadata);
        s3client.putObject(putObjectRequest);
    }



}
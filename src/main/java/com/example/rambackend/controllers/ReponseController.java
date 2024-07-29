package com.example.rambackend.controllers;
import com.example.rambackend.EmailService;
import com.example.rambackend.PdfService;
import com.example.rambackend.entities.Regle;
import com.example.rambackend.entities.RegleReponse;
import com.example.rambackend.entities.Reponse;
import com.example.rambackend.services.RegleService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import com.example.rambackend.services.ReponseService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/Reponse")
public class ReponseController {
    @Autowired
    private ReponseService reponseService;
    @Autowired
    private PdfService  generatePdfBytesForReponse;
    @Autowired
    private PdfService pdfService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<Reponse> addReponse(@RequestBody Reponse reponse) {
        Reponse savedReponse = reponseService.saveReponse(reponse);
        return ResponseEntity.ok(savedReponse);
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

        // Fetch the Reponse object using the reponseId
        Reponse reponse = reponseService.getReponseById(reponseId);
        if (reponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id " + reponseId);
        }

        // Generate PDF bytes using the Reponse object
        byte[] pdfBytes = pdfService.generatePdfBytesForReponse(reponse);
        String filename = "reponse_" + reponseId + ".pdf";

        // Send the email with the PDF attachment
        emailService.sendEmailWithAttachment("dalalaziz16@gmail.com", "Objet de l'Email", "Contenu de l'Email", pdfBytes, filename);

        return ResponseEntity.ok("Email envoyé");
    }

}

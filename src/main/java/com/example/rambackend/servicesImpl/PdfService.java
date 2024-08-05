package com.example.rambackend.servicesImpl;


import com.example.rambackend.entities.*;
import com.example.rambackend.repository.RapportAuditeRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.InputStream;


@Service
public class PdfService {


    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private RapportAuditeRepository rapportAuditeRepository;

    public byte[] generatePdf(Audit audit, List<RegleReponse> reponses) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);


            Image logo = new Image(ImageDataFactory.create(new ClassPathResource("static/images/logo.png").getURL()));
            logo.setWidth(100);
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(logo);


            // Ajout du titre
            Paragraph title = new Paragraph("Rapport d'Audit")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Formatage des dates
            String dateDebut = formatDate(audit.getDateDebut());
            String dateFin = formatDate(audit.getDateFin());


            Paragraph info = new Paragraph(
                    "Auditeur: " + audit.getAuditeur().getFullname() + "\n" +
                            "Ville d'escale: " + audit.getEscaleVille() + "\n" +
                            "Date de début: " + dateDebut + "\n" +
                            "Date de fin: " + dateFin
            )
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(info);

            // Création du tableau
            float[] columnWidths = {300F, 100F};
            Table table = new Table(columnWidths);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // En-têtes du tableau
            table.addHeaderCell(new Cell().add(new Paragraph("Règle").setTextAlignment(TextAlignment.CENTER).setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Réponse").setTextAlignment(TextAlignment.CENTER).setBold()));

            // Ajout des règles et réponses au tableau
            for (RegleReponse regleReponse : reponses) {
                table.addCell(new Cell().add(new Paragraph(regleReponse.getRegle().getDescription())));
                String reponseText = regleReponse.getValue() ? "Conforme" : "Non-Conforme";
                Cell reponseCell = new Cell().add(new Paragraph(reponseText).setTextAlignment(TextAlignment.CENTER));
                if (regleReponse.getValue()) {
                    reponseCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
                }
                table.addCell(reponseCell);
            }

            // Ajout du tableau au document
            document.add(table);

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generatePdfBytesForReponse(Reponse reponse) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            // Add logo
            try (InputStream logoStream = getClass().getResourceAsStream("/IMG/logoRAM.png")) {
                if (logoStream != null) {
                    Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()));
                    logo.scaleToFit(100, 100); // Scale logo to fit within 100x100 pixels (adjust as needed)
                    logo.setHorizontalAlignment(HorizontalAlignment.CENTER); // Center the logo
                    document.add(logo);
                } else {
                    System.err.println("Logo image not found.");
                }
            }

            // Title
            document.add(new Paragraph("Rapport d'Audit")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Filter non-conform rules
            List<RegleReponse> nonConformRules = reponse.getReponses().stream()
                    .filter(r -> !r.getValue())
                    .collect(Collectors.toList());

            if (!nonConformRules.isEmpty()) {
                // Add a table for non-conform rules
                Table table = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();

                // Header en gras
                table.addHeaderCell(new Cell().add(new Paragraph("Action Corrective")
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)));

                // Rows corrective actions
                for (RegleReponse regleReponse : nonConformRules) {
                    Regle regle = regleReponse.getRegle();
                    table.addCell(new Cell().add(new Paragraph(regle.getActionCorrective())
                            .setTextAlignment(TextAlignment.CENTER)));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("Aucune règle non conforme trouvée.")
                        .setTextAlignment(TextAlignment.CENTER));
            }

            document.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<InputStreamResource> getPdfById(String id) {
        try {
            GridFsResource resource = gridFsOperations.getResource(id);
            if (resource == null || !resource.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(resource.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public RapportAudite savePdfToRapportAudite(byte[] pdfBytes, String filename) {
        RapportAudite rapportAudite = new RapportAudite();
        rapportAudite.setId(ObjectId.get().toHexString());
        rapportAudite.setNom(filename);
        rapportAudite.setContenu(pdfBytes);

        return rapportAuditeRepository.save(rapportAudite);
    }

    private String formatDate(Object date) {
        if (date == null) {
            return "Non spécifié";
        }
        if (date instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.format((Date) date);
        }
        if (date instanceof LocalDate) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return ((LocalDate) date).format(formatter);
        }
        return date.toString(); // fallback
    }

}
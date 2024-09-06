package com.example.rambackend.servicesImpl;


import com.example.rambackend.entities.*;
import com.example.rambackend.enums.ReponseType;
import com.example.rambackend.repository.RapportAuditeRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import java.time.format.DateTimeFormatter;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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


    public byte[] generatePdf(Reponse reponse) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        try {
            addHeader(document, reponse.getAudit());
            addAuditInfo(document, reponse.getAudit());
            addReferential(document, reponse.getAudit());
            addAuditContext(document, reponse.getAudit());
            addScopeOfAudit(document);
            addInterviewees(document, reponse.getAudit().getPersonneRencontresees());
            addSummaryTable(document, reponse.getReponses());
            addNonConformitiesSummary(document, reponse.getReponses());
            addObservationsSummary(document, reponse.getReponses());
            addImprovementsSummary(document, reponse.getReponses());

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void addSummaryTable(Document document, List<RegleReponse> reponses) {
        document.add(new Paragraph("Summary").setBold().setFontSize(14));

        Table table = new Table(new float[]{3, 1});
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(new Cell().add(new Paragraph("Category").setBold()));
        table.addCell(new Cell().add(new Paragraph("Count").setBold()));

        long nonConformCount = reponses.stream().filter(r -> r.getValue() == ReponseType.NON_CONFORME).count();
        long observationCount = reponses.stream().filter(r -> r.getValue() == ReponseType.OBSERVATION).count();
        long improvementCount = reponses.stream().filter(r -> r.getValue() == ReponseType.AMELIORATION).count();
        long totalCorrectiveActions = nonConformCount + observationCount + improvementCount;

        table.addCell("Total number of corrective actions");
        table.addCell(String.valueOf(totalCorrectiveActions));

        table.addCell("Total number of non-conformities");
        table.addCell(String.valueOf(nonConformCount));

        table.addCell("Total number of observations");
        table.addCell(String.valueOf(observationCount));

        table.addCell("Total number of improvements");
        table.addCell(String.valueOf(improvementCount));

        document.add(table);
        document.add(new Paragraph("\n"));  // Add some space after the table
    }

    private void addHeader(Document document, Audit audit) {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1})).useAllAvailableWidth();
        headerTable.addCell(new Cell().add(new Paragraph("Audit report\n(Ground Handling Subcontractor)")).setBorder(Border.NO_BORDER));
        headerTable.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));

        String auditNumber = audit.getNumeroOrdre() != null ? audit.getNumeroOrdre().toString() : "N/A";
        String formattedDate = formatDateDDMMYYYY(LocalDateTime.now());

        headerTable.addCell(new Cell().add(new Paragraph("Audit N°" + auditNumber + "\nDate: " +formattedDate))
                .setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));

        document.add(headerTable);
    }

    private String formatDateDDMMYYYY(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }
    private void addAuditInfo(Document document, Audit audit) {
        document.add(new Paragraph("Object: Recurrent audit of " + audit.getEscaleVille() + " based at " + audit.getAeroport())
                .setBold());
    }

    private void addReferential(Document document, Audit audit) {
        Table referentialTable = new Table(UnitValue.createPercentArray(new float[]{2, 1})).useAllAvailableWidth();
        referentialTable.addCell(new Cell().add(new Paragraph("Referential\nNational Regulation\nRAM Procedures\nGround Handling Audit Check-list")));
        referentialTable.addCell(new Cell().add(new Paragraph("Entity:\n" + audit.getEscaleVille())));
        document.add(referentialTable);
    }

    private void addAuditContext(Document document, Audit audit) {
        document.add(new Paragraph("The recurrent audit of the handler " + audit.getHandlingProvider() + " is part of the process of the supervision of the handling outsourced by Royal Air Maroc."));
        document.add(new Paragraph("The audit took place on " + audit.getEscaleVille() + " at the office of the subcontractor of ground handling, which is located at " + audit.getAeroport() + " Airport."));
    }

    private void addScopeOfAudit(Document document) {
        document.add(new Paragraph("Scope of audit:").setBold());
        List<String> scopeItems = Arrays.asList(
                "Organisation", "Training", "Manual System", "Load Control", "Incident Response Plan",
                "Passenger Services", "Facilities for Passengers Requiring Special Assistance",
                "Baggage Services", "Baggage Recovery (Lost & Found)", "GSE Maintenance",
                "Potable Water & Toilet Services", "Non-Conformities Summary", "Observations Summary"
        );
        for (String item : scopeItems) {
            document.add(new Paragraph("• " + item));
        }
    }

    private void addInterviewees(Document document, List<PersonneRencontrees> personnes) {
        document.add(new Paragraph("Interviewees:").setBold());
        for (PersonneRencontrees personne : personnes) {
            document.add(new Paragraph("• " + personne.getFullname() + ": " + personne.getTitle()));
        }
    }

    private void addSummary(Document document, List<RegleReponse> reponses) {
        long nonConformCount = reponses.stream().filter(r -> r.getValue() == ReponseType.NON_CONFORME).count();
        long observationCount = reponses.stream().filter(r -> r.getValue() == ReponseType.OBSERVATION).count();
        long improvementCount = reponses.stream().filter(r -> r.getValue() == ReponseType.AMELIORATION).count();

        document.add(new Paragraph("Total number of corrective actions: " + (nonConformCount + observationCount + improvementCount)));
        document.add(new Paragraph("Total number of non-conformities: " + nonConformCount));
        document.add(new Paragraph("Total number of observations: " + observationCount));
        document.add(new Paragraph("Total number of improvements: " + improvementCount));
    }

    private void addNonConformitiesSummary(Document document, List<RegleReponse> reponses) {
        document.add(new Paragraph("A. Summary of Non-Conformities:").setBold());
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("References");
        table.addHeaderCell("Non-conformities");
        table.addHeaderCell("Comments");

        for (RegleReponse reponse : reponses) {
            if (reponse.getValue() == ReponseType.NON_CONFORME) {
                table.addCell(reponse.getRegle().getId());
                table.addCell(reponse.getRegle().getDescription());
                table.addCell(reponse.getCommentaire());
            }
        }
        document.add(table);
    }

    private void addObservationsSummary(Document document, List<RegleReponse> reponses) {
        document.add(new Paragraph("B. Summary of Observations:").setBold());
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("References");
        table.addHeaderCell("Observations");
        table.addHeaderCell("Comments");

        for (RegleReponse reponse : reponses) {
            if (reponse.getValue() == ReponseType.OBSERVATION) {
                table.addCell(reponse.getRegle().getId());
                table.addCell(reponse.getRegle().getDescription());
                table.addCell(reponse.getCommentaire());
            }
        }
        document.add(table);
    }

    private void addImprovementsSummary(Document document, List<RegleReponse> reponses) {
        document.add(new Paragraph("C. Total number of improvements:").setBold());
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("References");
        table.addHeaderCell("Improvements");
        table.addHeaderCell("Comments");

        for (RegleReponse reponse : reponses) {
            if (reponse.getValue() == ReponseType.AMELIORATION) {
                table.addCell(reponse.getRegle().getId());
                table.addCell(reponse.getRegle().getDescription());
                table.addCell(reponse.getCommentaire());
            }
        }
        document.add(table);
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Non spécifié";
    }
    private String getReponseText(ReponseType value) {
        switch (value) {
            case CONFORME:
                return "Conforme";
            case NON_CONFORME:
                return "Non-Conforme";
            case OBSERVATION:
                return "Observation";
            case AMELIORATION:
                return "Amélioration";
            default:
                return "Inconnu";
        }
    }

    private com.itextpdf.kernel.colors.Color getReponseColor(ReponseType value) {
        switch (value) {
            case CONFORME:
                return ColorConstants.LIGHT_GRAY;
            case NON_CONFORME:
                return ColorConstants.PINK;
            case OBSERVATION:
                return ColorConstants.YELLOW;
            case AMELIORATION:
                return ColorConstants.BLUE;
            default:
                return ColorConstants.WHITE;
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
            List<RegleReponse> nonConformAndObservations = reponse.getReponses().stream()
                    .filter(r -> r.getValue() == ReponseType.NON_CONFORME || r.getValue() == ReponseType.OBSERVATION)
                    .collect(Collectors.toList());

            if (!nonConformAndObservations.isEmpty()) {
                // Add a table for non-conform rules and observations
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();

                // Header en gras
                table.addHeaderCell(new Cell().add(new Paragraph("Type").setBold().setTextAlignment(TextAlignment.CENTER)));
                table.addHeaderCell(new Cell().add(new Paragraph("Action Corrective").setBold().setTextAlignment(TextAlignment.CENTER)));

                // Rows corrective actions
                for (RegleReponse regleReponse : nonConformAndObservations) {
                    Regle regle = regleReponse.getRegle();
                    table.addCell(new Cell().add(new Paragraph(getReponseText(regleReponse.getValue()))
                            .setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(regle.getActionCorrective())
                            .setTextAlignment(TextAlignment.CENTER)));
                }

                document.add(table);
            } else {
                document.add(new Paragraph("Aucune règle non conforme ou observation trouvée.")
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
package com.example.rambackend.servicesImpl;


import com.example.rambackend.entities.*;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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


@Service
public class PdfService {

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
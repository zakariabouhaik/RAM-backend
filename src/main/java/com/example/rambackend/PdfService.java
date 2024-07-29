package com.example.rambackend;


import com.example.rambackend.entities.Regle;
import com.example.rambackend.entities.Reponse;
import com.example.rambackend.entities.RegleReponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PdfService {

    public byte[] generatePdfBytesForReponse(Reponse reponse) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            // logo
            try {
                InputStream logoStream = getClass().getResourceAsStream("/IMG/logoRAM.png");
                if (logoStream != null) {
                    Image logo = Image.getInstance(ImageIO.read(logoStream), null);
                    logo.scaleToFit(100, 100); // Scale logo to fit within 100x100 pixels (adjust as needed)
                    logo.setAlignment(Image.ALIGN_CENTER); // Center the logo
                    document.add(logo);
                } else {
                    System.err.println("Logo image not found.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Title
            document.add(new Paragraph("Rapport d'Audit"));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Filter non-conform rules
            List<RegleReponse> nonConformRules = reponse.getReponses().stream()
                    .filter(r -> !r.getValue())
                    .collect(Collectors.toList());

            if (!nonConformRules.isEmpty()) {
                // Add a table for non-conform rules
                PdfPTable table = new PdfPTable(1);

                //header en gras
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                PdfPCell headerCell = new PdfPCell(new Paragraph("Action Corrective", boldFont));
                table.addCell(headerCell);


                // rows corrective actions
                for (RegleReponse regleReponse : nonConformRules) {
                    Regle regle = regleReponse.getRegle();
                    PdfPCell actionCell = new PdfPCell(new Paragraph(regle.getActionCorrective()));
                    table.addCell(actionCell);
                }

                document.add(table);
            } else {
                document.add(new Paragraph("Aucune règle non conforme trouvée."));
            }

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }


}

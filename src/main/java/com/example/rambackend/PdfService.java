package com.example.rambackend;

import com.example.rambackend.repository.RapportAuditeRepository;
import com.example.rambackend.entities.RapportAudite;
import com.example.rambackend.entities.Regle;
import com.example.rambackend.entities.Reponse;
import com.example.rambackend.entities.RegleReponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;


    @Autowired
    private RapportAuditeRepository rapportAuditeRepository;

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

    ///////////////////////////////////////////////////////////////////////////////
    public ResponseEntity<InputStreamResource> getPdfById(String id) {
        try {
            // Retrieve file from GridFS
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

        // Save the RapportAudite and return it
        return rapportAuditeRepository.save(rapportAudite);
    }
}
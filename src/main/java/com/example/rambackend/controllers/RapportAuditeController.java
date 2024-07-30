package com.example.rambackend.controllers;

import com.example.rambackend.entities.RapportAudite;
import com.example.rambackend.repository.RapportAuditeRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rapport-audite")
public class RapportAuditeController {
        @Autowired
        private RapportAuditeRepository rapportAuditeRepository;

        @PostMapping
        public RapportAudite savePdfToRapportAudite(byte[] pdfBytes, String filename) {
            RapportAudite rapportAudite = new RapportAudite();
            rapportAudite.setId(ObjectId.get().toHexString()); //ID as a String
            rapportAudite.setNom(filename);
            rapportAudite.setContenu(pdfBytes);
            return rapportAuditeRepository.save(rapportAudite);
        }
}



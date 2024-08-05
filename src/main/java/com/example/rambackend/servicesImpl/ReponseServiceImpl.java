package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.*;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.repository.FormulaireRepository;
import com.example.rambackend.repository.RegleRepository;
import com.example.rambackend.repository.ReponseRepository;
import com.example.rambackend.services.ReponseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReponseServiceImpl implements ReponseService {
    @Autowired
    private ReponseRepository reponseRepository;

    @Autowired
    private FormulaireRepository formulaireRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private RegleRepository regleRepository;

    @Override
    public Reponse saveReponse(Reponse reponse) {
        if (reponse.getReponses() != null) {
            for (RegleReponse regleReponse : reponse.getReponses()) {
                Regle fullRegle = regleRepository.findById(regleReponse.getRegle().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Regle not found with id: " + regleReponse.getRegle().getId()));
                regleReponse.setRegle(fullRegle);
            }
        }

        if (reponse.getAudit() != null && reponse.getAudit().getId() != null) {
            Audit audit = auditRepository.findById(reponse.getAudit().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Audit not found with id: " + reponse.getAudit().getId()));
            reponse.setAudit(audit);
        }

        return reponseRepository.save(reponse);
    }
    @Override
    public List<Reponse> getAllReponses() {
        return reponseRepository.findAll();
    }

    @Override
    public Reponse getReponseById(String id) {
        return reponseRepository.findById(id).orElse(null);
    }

    @Override
    public Reponse updateReponse(String id, Reponse reponse) {
        Reponse currReponse = getReponseById(id);
        currReponse.setReponses(reponse.getReponses());
        return reponseRepository.save(currReponse);
    }

    @Override
    public void deleteReponseById(String id) {
        if (reponseRepository.existsById(id)) {
            reponseRepository.deleteById(id);
        }
    }
}

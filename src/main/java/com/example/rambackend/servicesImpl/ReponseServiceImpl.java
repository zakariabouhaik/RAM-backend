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
import java.util.stream.Collectors;

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
        if (reponse.getAudit() != null && reponse.getAudit().getId() != null) {
            Audit audit = auditRepository.findById(reponse.getAudit().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Audit not found with id: " + reponse.getAudit().getId()));
            reponse.setAudit(audit);
        }

        List<Reponse> existingReponses1 = reponseRepository.findAllByAuditId(reponse.getAudit().getId());
        Reponse existingReponse = existingReponses1.isEmpty() ? null : existingReponses1.get(0);

        if (existingReponse != null) {
            // Update existing response
            Map<String, RegleReponse> existingReponses = new HashMap<>();
            for (RegleReponse existingRegleReponse : existingReponse.getReponses()) {
                existingReponses.put(existingRegleReponse.getRegle().getId(), existingRegleReponse);
            }

            for (RegleReponse newRegleReponse : reponse.getReponses()) {
                if (newRegleReponse.getValue() != null) {
                    RegleReponse existingRegleReponse = existingReponses.get(newRegleReponse.getRegle().getId());
                    if (existingRegleReponse != null) {
                        existingRegleReponse.setValue(newRegleReponse.getValue());
                        existingRegleReponse.setNonConformeLevel(newRegleReponse.getNonConformeLevel());
                        existingRegleReponse.setCommentaire(newRegleReponse.getCommentaire());
                    } else {
                        Regle fullRegle = regleRepository.findById(newRegleReponse.getRegle().getId())
                                .orElseThrow(() -> new EntityNotFoundException("Regle not found with id: " + newRegleReponse.getRegle().getId()));
                        newRegleReponse.setRegle(fullRegle);
                        existingReponse.getReponses().add(newRegleReponse);
                    }
                }
            }
            existingReponse.setTemporary(true);
            return reponseRepository.save(existingReponse);
        } else {
            // Create new response
            List<RegleReponse> validReponses = new ArrayList<>();
            for (RegleReponse regleReponse : reponse.getReponses()) {
                if (regleReponse.getValue() != null) {
                    Regle fullRegle = regleRepository.findById(regleReponse.getRegle().getId())
                            .orElseThrow(() -> new EntityNotFoundException("Regle not found with id: " + regleReponse.getRegle().getId()));
                    regleReponse.setRegle(fullRegle);
                    validReponses.add(regleReponse);
                }
            }
            reponse.setReponses(validReponses);
            reponse.setTemporary(true);
            return reponseRepository.save(reponse);
        }
    }


    public void deleteRegleReponsesByAuditId(String auditId, List<String> regleIds) {
        List<Reponse> reponses = reponseRepository.findAllByAuditId(auditId);
        if (!reponses.isEmpty()) {
            Reponse reponse = reponses.get(0);
            List<RegleReponse> updatedReponses = reponse.getReponses().stream()
                    .filter(rr -> !regleIds.contains(rr.getRegle().getId()))
                    .collect(Collectors.toList());
            reponse.setReponses(updatedReponses);
            reponseRepository.save(reponse);
        }
    }
    @Override
    public Reponse getReponseByAuditId(String auditId) {
        List<Reponse> reponses = reponseRepository.findAllByAuditId(auditId);
        return reponses.isEmpty() ? null : reponses.get(0);
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
        if (currReponse != null) {
            currReponse.setReponses(reponse.getReponses());
            currReponse.setTemporary(reponse.isTemporary());
            return reponseRepository.save(currReponse);
        }
        return null;
    }

    @Override
    public void deleteReponseById(String id) {
        if (reponseRepository.existsById(id)) {
            reponseRepository.deleteById(id);
        }
    }
}
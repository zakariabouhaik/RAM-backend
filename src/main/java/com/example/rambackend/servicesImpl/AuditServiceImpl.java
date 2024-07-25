package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Formulaire;
import com.example.rambackend.entities.Utilisateur;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.repository.FormulaireRepository;
import com.example.rambackend.repository.UserRepository;
import com.example.rambackend.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class AuditServiceImpl implements AuditService {
    @Autowired
    private  AuditRepository auditRepository;
    @Autowired
    private FormulaireRepository formulaireRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Audit saveAudit(Audit audit) {


        Formulaire formulaire = formulaireRepository.findById(audit.getFormulaire().getId())
                .orElse(null);

        Utilisateur utilisateur = userRepository.findById(audit.getAuditeur().getId()).orElse(null);

        audit.setAuditeur(utilisateur);
        audit.setFormulaire(formulaire);
        return auditRepository.save(audit);
    }

    @Override
    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }

    @Override
    public Audit getAuditById(String id) {
        return auditRepository.findById(id).get();
    }

    @Override
    public void deleteAuditById(String id) {
        auditRepository.deleteById(id);
    }

    @Override
    public Audit updateAudit(String id, Audit audit) {
        Audit currAudit = getAuditById(id);
        currAudit.setEscaleVille(audit.getEscaleVille());
        currAudit.setDateProgramme(audit.getDateProgramme());
        currAudit.setDateDebut(audit.getDateDebut());
        currAudit.setDateFin(audit.getDateFin());
        currAudit.setArchivee(audit.isArchivee());
        currAudit.setFormulaire(audit.getFormulaire());
        currAudit.setRapportAudit(audit.getRapportAudit());
        currAudit.setRapportAction(audit.getRapportAction());
        return auditRepository.save(currAudit);

    }
}

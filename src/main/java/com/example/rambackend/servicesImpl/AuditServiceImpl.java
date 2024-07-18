package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.repository.AuditRepository;
import com.example.rambackend.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class AuditServiceImpl implements AuditService {
    @Autowired
    private  AuditRepository auditRepository;
    @Override
    public Audit saveAudit(Audit audit) {
        return auditRepository.save(audit);
    }

    @Override
    public List<Audit> getAllAudits() {
        return auditRepository.findAll();
    }

    @Override
    public Audit getAuditById(Long id) {
        return auditRepository.findById(id).get();
    }

    @Override
    public void deleteAuditById(Long id) {
        auditRepository.deleteById(id);
    }

    @Override
    public Audit updateAudit(Long id, Audit audit) {
        Audit currAudit = getAuditById(id);
        currAudit.setEscaleVille(audit.getEscaleVille());
        currAudit.setDateProgramme(audit.getDateProgramme());
        currAudit.setDateDebut(audit.getDateDebut());
        currAudit.setDateFin(audit.getDateFin());
        currAudit.setArchivee(audit.isArchivee());
        currAudit.setNomFormulaire(audit.getNomFormulaire());
        currAudit.setRapportAudit(audit.getRapportAudit());
        currAudit.setRapportAction(audit.getRapportAction());
        return auditRepository.save(currAudit);

    }
}

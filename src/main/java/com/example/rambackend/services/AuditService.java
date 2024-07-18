package com.example.rambackend.services;

import com.example.rambackend.entities.Audit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditService {
    Audit saveAudit(Audit audit);
    List<Audit> getAllAudits();
    Audit getAuditById(Long id);
    void deleteAuditById(Long id);
    Audit updateAudit(Long id, Audit audit);



}

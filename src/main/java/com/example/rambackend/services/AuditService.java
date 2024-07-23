package com.example.rambackend.services;

import com.example.rambackend.entities.Audit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditService {
    Audit saveAudit(Audit audit);
    List<Audit> getAllAudits();
    Audit getAuditById(String id);
    void deleteAuditById(String id);
    Audit updateAudit(String id, Audit audit);



}

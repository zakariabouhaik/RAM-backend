package com.example.rambackend.services;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.Generalities;
import com.example.rambackend.entities.PersonneRencontrees;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditService {
    Audit saveAudit(Audit audit);
    List<Audit> getAllAudits();
    Audit getAuditById(String id);
    void deleteAuditById(String id);
    Audit updateAudit(String id, Audit audit);

     List<Audit> findAuditsByUserId(String userId);
     List<Audit>  findAuditsByAuditeId(String userId);

     Audit saveGeneralitie(String id, Generalities generalities);

     Audit sendGeneralities(String id);
    Audit savePersonnesRencontrees(String id, List<PersonneRencontrees> personnes);






}

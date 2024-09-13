package com.example.rambackend.services;

import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.entities.ActionCorrectiveRegister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ActionCorrectiveRegisterService {

    ActionCorrectiveRegister saveActionCorrectiveActionCorrectiveRegister(ActionCorrectiveRegister actionCorrective);
    ActionCorrectiveRegister getActionCorrectiveActionCorrectiveRegisterById(String id);
    List<ActionCorrectiveRegister> getAllActionCorrectivesActionCorrectiveRegister();
    void deleteActionCorrectiveRegister(String id);
     public List<ActionCorrectiveRegister> findByAuditId(String auditId);

    ActionCorrectiveRegister updateStatus(String id, String status);

}

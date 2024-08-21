package com.example.rambackend.services;

import com.example.rambackend.entities.ActionCorrectiveStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ActionCorrectiveStatusService {
    public ActionCorrectiveStatus getStatus(String userId, String auditId);
    public ActionCorrectiveStatus updateStatus(ActionCorrectiveStatus status);
    public void saveAll(List<ActionCorrectiveStatus> actionCorrectiveStatuses);
    public void saveOrUpdate(ActionCorrectiveStatus status) ;
    public void markAsSent(String userId, String auditId);
    public boolean isSent(String userId, String auditId);
    }
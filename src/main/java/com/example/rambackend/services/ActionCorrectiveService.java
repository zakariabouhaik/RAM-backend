package com.example.rambackend.services;

import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.entities.Audit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ActionCorrectiveService {
    ActionCorrective saveActionCorrective(ActionCorrective actionCorrective);
    ActionCorrective getActionCorrectiveById(String id);
    List<ActionCorrective> getAllActionCorrectives();
    void deleteActionCorrective(String id);
    ActionCorrective updateActionCorrective(String id, ActionCorrective actionCorrective);

}

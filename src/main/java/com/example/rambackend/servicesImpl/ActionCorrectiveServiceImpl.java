package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.repository.ActionCorrectiveRepository;
import com.example.rambackend.services.ActionCorrectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActionCorrectiveServiceImpl implements ActionCorrectiveService {

    @Autowired
    private ActionCorrectiveRepository actionCorrectiveRepository;

    @Override
    public ActionCorrective saveActionCorrective(ActionCorrective actionCorrective) {
        return actionCorrectiveRepository.save(actionCorrective);
    }


    @Override
    public ActionCorrective getActionCorrectiveById(String id) {
        Optional<ActionCorrective> actionCorrective = actionCorrectiveRepository.findById(id);
        return actionCorrective.orElse(null);
    }

    @Override
    public List<ActionCorrective> getAllActionCorrectives() {
        return actionCorrectiveRepository.findAll();
    }

    @Override
    public void deleteActionCorrective(String id) {
        actionCorrectiveRepository.deleteById(id);
    }

    @Override
    public ActionCorrective updateActionCorrective(String id, ActionCorrective updatedActionCorrective) {
        ActionCorrective currActionCorrective = getActionCorrectiveById(id);
        if (currActionCorrective != null) {
            // Update the list of descriptions
            currActionCorrective.setDescriptions(updatedActionCorrective.getDescriptions());
            return actionCorrectiveRepository.save(currActionCorrective);
        }
        return null;
    }

    public List<ActionCorrective> findByAuditId(String auditId) {
        return actionCorrectiveRepository.findByAuditId(auditId);
    }



}
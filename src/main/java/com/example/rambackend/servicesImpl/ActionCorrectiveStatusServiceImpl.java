package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.ActionCorrectiveStatus;
import com.example.rambackend.repository.ActionCorrectiveStatusRepository;
import com.example.rambackend.services.ActionCorrectiveStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionCorrectiveStatusServiceImpl implements ActionCorrectiveStatusService {
    @Autowired
    private ActionCorrectiveStatusRepository repository;


    @Override
    public ActionCorrectiveStatus updateStatus(ActionCorrectiveStatus status) {
        return repository.save(status);
    }
    @Override
    public ActionCorrectiveStatus getStatus(String userId, String auditId) {
        return repository.findByUserIdAndAuditId(userId, auditId)
                .orElse(new ActionCorrectiveStatus());
    }

    @Override
    public void saveAll(List<ActionCorrectiveStatus> actionCorrectiveStatuses) {
        repository.saveAll(actionCorrectiveStatuses);
    }

    @Override
    public void saveOrUpdate(ActionCorrectiveStatus status) {
        Optional<ActionCorrectiveStatus> existingStatus = repository.findByUserIdAndAuditId(status.getUserId(), status.getAuditId());
        if (existingStatus.isPresent()) {
            ActionCorrectiveStatus statusToUpdate = existingStatus.get();
            statusToUpdate.setActionsState(status.getActionsState());
            repository.save(statusToUpdate);
        } else {
            repository.save(status);
        }
    }
}


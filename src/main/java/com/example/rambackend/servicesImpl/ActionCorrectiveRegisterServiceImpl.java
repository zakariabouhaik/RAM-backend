package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.entities.ActionCorrectiveRegister;
import com.example.rambackend.repository.ActionCorrectiveRegisterRepository;
import com.example.rambackend.services.ActionCorrectiveRegisterService;
import com.example.rambackend.services.ActionCorrectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActionCorrectiveRegisterServiceImpl implements ActionCorrectiveRegisterService {

    @Autowired
      ActionCorrectiveRegisterRepository actionCorrectiveRegisterRepository;


    @Override
    public ActionCorrectiveRegister saveActionCorrectiveActionCorrectiveRegister(ActionCorrectiveRegister actionCorrectiveRegister) {
        return actionCorrectiveRegisterRepository.save(actionCorrectiveRegister);

    }

    @Override
    public ActionCorrectiveRegister getActionCorrectiveActionCorrectiveRegisterById(String id) {
        Optional<ActionCorrectiveRegister> actionCorrectiveRegister = actionCorrectiveRegisterRepository.findById(id);
        return actionCorrectiveRegister.orElse(null);
    }

    @Override
    public List<ActionCorrectiveRegister> getAllActionCorrectivesActionCorrectiveRegister() {
        return actionCorrectiveRegisterRepository.findAll();
    }

    @Override
    public void deleteActionCorrectiveRegister(String id) {
        actionCorrectiveRegisterRepository.deleteById(id);
    }


    @Override
    public List<ActionCorrectiveRegister> findByAuditId(String auditId) {
        return actionCorrectiveRegisterRepository.findByAuditId(auditId);
    }


    @Override
    public ActionCorrectiveRegister updateStatus(String id, String status) {
        Optional<ActionCorrectiveRegister> registerOpt = actionCorrectiveRegisterRepository.findById(id);
        if (registerOpt.isPresent()) {
            ActionCorrectiveRegister register = registerOpt.get();
            register.setStatus(status);
            return actionCorrectiveRegisterRepository.save(register);
        }
        return null;
    }

}

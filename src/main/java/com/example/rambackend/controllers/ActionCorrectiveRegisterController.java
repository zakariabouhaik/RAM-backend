package com.example.rambackend.controllers;


import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.entities.ActionCorrectiveRegister;
import com.example.rambackend.entities.ActionCorrectiveStatus;
import com.example.rambackend.repository.ActionCorrectiveRegisterRepository;
import com.example.rambackend.repository.ActionCorrectiveRepository;
import com.example.rambackend.services.ActionCorrectiveRegisterService;
import com.example.rambackend.services.ActionCorrectiveStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/ActionCorrectiveRegister")
public class ActionCorrectiveRegisterController {

    @Autowired
    private ActionCorrectiveRegisterService actionCorrectiveRegisterService;

    @Autowired
    ActionCorrectiveRegisterRepository actionCorrectiveRegisterRepository;

    @PostMapping
    public ResponseEntity<ActionCorrectiveRegister> createActionCorrective(@RequestBody ActionCorrectiveRegister actionCorrective) {
         ActionCorrectiveRegister savedActionCorrective = actionCorrectiveRegisterService.saveActionCorrectiveActionCorrectiveRegister(actionCorrective);
        return ResponseEntity.ok(savedActionCorrective);
    }


    @PostMapping("/save-bulk")
    public ResponseEntity<List<ActionCorrectiveRegister>> saveActionCorrectives(@RequestBody List<ActionCorrectiveRegister> actionCorrectives) {
        List<ActionCorrectiveRegister> savedActionCorrectives = actionCorrectiveRegisterRepository.saveAll(actionCorrectives);
        return ResponseEntity.ok(savedActionCorrectives);
    }



    @GetMapping("/{id}")
    public ActionCorrectiveRegister getActionCorrectiveById(@PathVariable String id) {
        return actionCorrectiveRegisterService.getActionCorrectiveActionCorrectiveRegisterById(id);
    }

    @GetMapping
    public List<ActionCorrectiveRegister> getAllActionCorrectives() {
        return actionCorrectiveRegisterService.getAllActionCorrectivesActionCorrectiveRegister();
    }



    @DeleteMapping("/{id}")
    public void deleteActionCorrective(@PathVariable String id) {
        actionCorrectiveRegisterService.deleteActionCorrectiveRegister(id);
    }


    @GetMapping("/audit/{auditId}")
    public ResponseEntity<List<ActionCorrectiveRegister>> getActionCorrectivesByAuditId(@PathVariable String auditId) {
        List<ActionCorrectiveRegister> actionCorrectives = actionCorrectiveRegisterService.findByAuditId(auditId);
        return ResponseEntity.ok(actionCorrectives);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ActionCorrectiveRegister> updateStatus(@PathVariable String id, @RequestParam String status) {
        ActionCorrectiveRegister updatedRegister = actionCorrectiveRegisterService.updateStatus(id, status);
        if (updatedRegister != null) {
            return ResponseEntity.ok(updatedRegister);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}

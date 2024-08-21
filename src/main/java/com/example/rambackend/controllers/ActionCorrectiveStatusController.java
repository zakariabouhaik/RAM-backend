package com.example.rambackend.controllers;

import com.example.rambackend.entities.ActionCorrectiveStatus;
import com.example.rambackend.repository.ActionCorrectiveStatusRepository;
import com.example.rambackend.services.ActionCorrectiveStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ActionCorrectiveStatus")
public class ActionCorrectiveStatusController {

    @Autowired
    private ActionCorrectiveStatusService actionCorrectiveStatusService;

    @GetMapping("/{userId}/{auditId}")
    public ActionCorrectiveStatus getStatus(@PathVariable String userId, @PathVariable String auditId) {
        return actionCorrectiveStatusService.getStatus(userId, auditId);
    }


    @PostMapping("/saveOrUpdate")
    public ResponseEntity<?> saveOrUpdateActionCorrectiveStatus(@RequestBody ActionCorrectiveStatus actionCorrectiveStatus) {
        try {
            actionCorrectiveStatusService.saveOrUpdate(actionCorrectiveStatus);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/markAsSent/{userId}/{auditId}")
    public ResponseEntity<?> markAsSent(@PathVariable String userId, @PathVariable String auditId) {
        try {
            actionCorrectiveStatusService.markAsSent(userId, auditId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/isSent/{userId}/{auditId}")
    public ResponseEntity<Boolean> isSent(@PathVariable String userId, @PathVariable String auditId) {
        boolean sent = actionCorrectiveStatusService.isSent(userId, auditId);
        return ResponseEntity.ok(sent);
    }

}

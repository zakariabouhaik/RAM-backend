package com.example.rambackend.controllers;
import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.services.ActionCorrectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ActionCorrective")
public class ActionCorrectiveController {

    @Autowired
    private ActionCorrectiveService actionCorrectiveService;

    @PostMapping
    public ActionCorrective createActionCorrective(@RequestBody ActionCorrective actionCorrective) {
        return actionCorrectiveService.saveActionCorrective(actionCorrective);
    }

    @GetMapping("/{id}")
    public ActionCorrective getActionCorrectiveById(@PathVariable String id) {
        return actionCorrectiveService.getActionCorrectiveById(id);
    }

    @GetMapping
    public List<ActionCorrective> getAllActionCorrectives() {
        return actionCorrectiveService.getAllActionCorrectives();
    }

    @PutMapping("/{id}")
    public ActionCorrective updateActionCorrective(@PathVariable String id, @RequestBody ActionCorrective updatedActionCorrective) {
        return actionCorrectiveService.updateActionCorrective(id, updatedActionCorrective);
    }

    @DeleteMapping("/{id}")
    public void deleteActionCorrective(@PathVariable String id) {
        actionCorrectiveService.deleteActionCorrective(id);
    }
}
package com.example.rambackend.controllers;
import com.amazonaws.Response;
import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.repository.ActionCorrectiveRepository;
import com.example.rambackend.services.ActionCorrectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ActionCorrective")
public class ActionCorrectiveController {

    @Autowired
    private ActionCorrectiveService actionCorrectiveService;
    @Autowired
    private ActionCorrectiveRepository actionCorrectiveRepository;

    @PostMapping("/save")

    public ActionCorrective createActionCorrective(@RequestBody ActionCorrective actionCorrective) {
        return actionCorrectiveService.saveActionCorrective(actionCorrective);
    }

    @PostMapping("/save-bulk")
    public ResponseEntity<List<ActionCorrective>> saveActionCorrectives(@RequestBody List<ActionCorrective> actionCorrectives) {
        List<ActionCorrective> savedActionCorrectives = actionCorrectiveRepository.saveAll(actionCorrectives);
        return ResponseEntity.ok(savedActionCorrectives);
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

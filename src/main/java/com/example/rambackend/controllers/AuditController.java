package com.example.rambackend.controllers;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Audit")

public class AuditController {
    @Autowired
    private AuditService auditService;

    @PostMapping
    public Audit addAudit(@RequestBody Audit audit) {
        return auditService.saveAudit(audit);
    }

    @GetMapping()
    private List<Audit> getAllAudits(){
        return auditService.getAllAudits();
    }

    @GetMapping("/{id}")
    public Audit getAudit(@PathVariable String id) {
        return auditService.getAuditById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAudit(@PathVariable String id) {
        auditService.deleteAuditById(id);
    }

    @PutMapping("/{id}")
    public Audit editAudit(@PathVariable String id, @RequestBody Audit audit) {
        return auditService.updateAudit(id, audit);
    }






}

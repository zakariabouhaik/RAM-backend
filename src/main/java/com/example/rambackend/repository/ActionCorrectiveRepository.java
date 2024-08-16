package com.example.rambackend.repository;

import com.example.rambackend.entities.ActionCorrective;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionCorrectiveRepository extends MongoRepository<ActionCorrective, String> {
    List<ActionCorrective> findByAuditId(String auditId);
}

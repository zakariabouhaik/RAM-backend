package com.example.rambackend.repository;

import com.example.rambackend.entities.ActionCorrectiveStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActionCorrectiveStatusRepository extends MongoRepository<ActionCorrectiveStatus, String> {
    Optional<ActionCorrectiveStatus> findByUserIdAndAuditId(String userId, String auditId);
}

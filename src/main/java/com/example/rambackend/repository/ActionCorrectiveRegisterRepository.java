package com.example.rambackend.repository;

import com.example.rambackend.entities.ActionCorrective;
import com.example.rambackend.entities.ActionCorrectiveRegister;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionCorrectiveRegisterRepository extends MongoRepository<ActionCorrectiveRegister,String> {
    List<ActionCorrectiveRegister> findByAuditId(String auditId);

}

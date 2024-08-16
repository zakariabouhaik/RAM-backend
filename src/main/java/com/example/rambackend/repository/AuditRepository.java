package com.example.rambackend.repository;

import com.example.rambackend.entities.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditRepository extends MongoRepository<Audit, String> {
    List<Audit> findByAuditeurId(String auditeurId);
    List<Audit> findByAuditeId(String auditeId);

}

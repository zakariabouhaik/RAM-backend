package com.example.rambackend.repository;

import com.example.rambackend.entities.ActionCorrective;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActionCorrectiveRepository extends MongoRepository<ActionCorrective, String> {
}

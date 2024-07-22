package com.example.rambackend.repository;

import com.example.rambackend.entities.Regle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegleRepository extends MongoRepository<Regle,String> {
}

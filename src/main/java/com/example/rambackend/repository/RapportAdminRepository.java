package com.example.rambackend.repository;

import com.example.rambackend.entities.RapportAdmin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RapportAdminRepository  extends MongoRepository<RapportAdmin,String> {
}

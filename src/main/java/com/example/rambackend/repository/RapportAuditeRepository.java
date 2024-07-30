package com.example.rambackend.repository;

import com.example.rambackend.entities.RapportAudite;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RapportAuditeRepository extends MongoRepository<RapportAudite,String> {

}

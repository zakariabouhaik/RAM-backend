package com.example.rambackend.repository;

import com.example.rambackend.entities.Formulaire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormulaireRepository  extends MongoRepository<Formulaire,String> {
}

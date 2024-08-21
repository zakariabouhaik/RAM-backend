package com.example.rambackend.repository;

import com.example.rambackend.entities.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<Utilisateur,String> {
 }

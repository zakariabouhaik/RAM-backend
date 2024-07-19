package com.example.rambackend.repository;

import com.example.rambackend.entities.Regle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegleRepository extends JpaRepository<Regle,Long> {
}

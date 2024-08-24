package com.example.rambackend.repository;


import com.example.rambackend.entities.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReponseRepository extends MongoRepository<Reponse,String> {
    List<Reponse> findAllByAuditId(String auditId);
    void deleteRegleReponsesByAuditId(String auditId, List<String> regleIds);


}

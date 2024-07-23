
package com.example.rambackend.repository;

import com.example.rambackend.entities.Section;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends MongoRepository<Section,String> {
}

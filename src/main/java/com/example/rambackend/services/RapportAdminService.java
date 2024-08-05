package com.example.rambackend.services;

import com.example.rambackend.entities.Audit;
import com.example.rambackend.entities.RapportAdmin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RapportAdminService {
    RapportAdmin saverapportAdmin(RapportAdmin rapportAdmin);
    List<RapportAdmin> getAllrapportAdmins();
    RapportAdmin getrapportAdminById(String id);
    void deleterapportAdminById(String id);

}

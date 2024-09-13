package com.example.rambackend.entities;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.util.List;

@Data
@Document
public class ActionCorrectiveRegister {

    @Id
    private String id;
    private Audit audit;
    private String rootcause;
    private String responsable;
    private String implementationofthecorrectiveaction;
    private String responsibleoftheprocessus;
    private List<UploadedFile> uploadedFiles;
    private Registerdornot registerdornot;
    private String status;

}

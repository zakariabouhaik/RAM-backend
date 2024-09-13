package com.example.rambackend.entities;

import lombok.Data;

@Data

public class UploadedFile {
    private String publicId;
    private String url;
    private String resourceType;
    private String format;
}

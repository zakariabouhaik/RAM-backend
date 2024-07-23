package com.example.rambackend.services;

import com.example.rambackend.entities.Section;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface SectionService {
    Section saveSection(Section section);
    List<Section> getAllSections();
    Section getSectionById(String id);
    Section updateSection(String id, Section section);
    void deleteSectionById(String id);
}


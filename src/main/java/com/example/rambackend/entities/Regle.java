package com.example.rambackend.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@Document
public class Regle {
        @Id
        private String id;
        private String description;


       // private ActionCorrective actionCorrective;


        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Regle regle = (Regle) o;
                return Objects.equals(id, regle.id);
        }


        @Override
        public int hashCode() {
                return Objects.hash(id);
        }
}

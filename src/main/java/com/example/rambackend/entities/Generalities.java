package com.example.rambackend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Generalities {
    private String summaryofairlinesassisted;
    private int numberofflightshandledperdayinmonth ;

    private int numberofcheckinandboardingagents;

    private int numberoframpagents ;

    private int numberofsupervisors;

    private int numberofgsemaintenance;

}

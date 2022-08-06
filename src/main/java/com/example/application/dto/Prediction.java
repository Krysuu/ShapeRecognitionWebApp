package com.example.application.dto;

import lombok.Data;

@Data
public class Prediction {
    private String label;
    private Double score;
}

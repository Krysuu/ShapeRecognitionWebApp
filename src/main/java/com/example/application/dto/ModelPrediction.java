package com.example.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class ModelPrediction {
    private List<Prediction> predictions;
}

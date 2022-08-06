package com.example.application.views.util;

import com.example.application.dto.ModelPrediction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayOutputStream;

@Data
@AllArgsConstructor
public class ResultHolder {
    private ModelPrediction prediction;
    private ByteArrayOutputStream image;
}

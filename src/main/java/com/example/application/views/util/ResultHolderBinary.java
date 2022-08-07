package com.example.application.views.util;

import com.example.application.dto.Prediction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayOutputStream;

@Data
@AllArgsConstructor
public class ResultHolderBinary {
    private Prediction prediction;
    private ByteArrayOutputStream image;
}

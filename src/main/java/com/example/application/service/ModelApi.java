package com.example.application.service;

import com.example.application.dto.ModelPrediction;
import com.example.application.dto.Prediction;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Arrays;

@Service
@AllArgsConstructor
public class ModelApi {
    private final static String SERVER_ADDRESS = "http://localhost:8000/";

    public ModelPrediction predictImageStandard(File image) {
        var response = predictImage(image, "models/standard");
        var result = new ModelPrediction();
        result.setPredictions(Arrays.asList(response.getBody()));
        return result;
    }

    public Prediction predictImageBinary(File image, String className) {
        var response = predictImage(image, "models/" + className);
        return response.getBody()[0];
    }


    private ResponseEntity<Prediction[]> predictImage(File image, String model) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(image));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);
        var restTemplate = new RestTemplate();
        return restTemplate.postForEntity(SERVER_ADDRESS + model, requestEntity, Prediction[].class);
    }
}

package com.example.application.views;

import com.example.application.dto.Prediction;
import com.example.application.service.FileService;
import com.example.application.service.ModelApi;
import com.example.application.views.util.UploadPolishI18N;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;


@PageTitle("Klasyfikacja binarna")
@Route(value = "binarna", layout = MainLayout.class)
@RouteAlias(value = "binarna", layout = MainLayout.class)
@Slf4j
public class BinaryView extends VerticalLayout implements HasUrlParameter<String> {

    private final ModelApi modelApi;
    private final FileService fileService;
    private final HorizontalLayout predictionLayout = new HorizontalLayout();
    private Upload singleFileUpload;
    private final FileBuffer fileBuffer = new FileBuffer();
    private Image predImage = new Image();
    private String selectedModel;

    public BinaryView(ModelApi modelApi, FileService fileService) {
        this.modelApi = modelApi;
        this.fileService = fileService;
        init();
    }

    private void init() {
        singleFileUpload = new Upload(fileBuffer);
        singleFileUpload.setI18n(new UploadPolishI18N());
        singleFileUpload.addSucceededListener(event -> processFile());
        singleFileUpload.setWidth("35%");
        singleFileUpload.setHeight("100%");

        predImage.setMaxWidth("65%");
        predImage.setMaxHeight("100%");

        var upperLayout = new HorizontalLayout(singleFileUpload, predImage);
        upperLayout.setHeight("50%");
        upperLayout.setWidth("100%");

        updatePredictionLayout(null);
        add(upperLayout, predictionLayout);
    }

    @SneakyThrows
    private void processFile() {
        var baos = new ByteArrayOutputStream();
        fileBuffer.getInputStream().transferTo(baos);

        InputStream predictionInputStream = new ByteArrayInputStream(baos.toByteArray());
        InputStream imageInputStream = new ByteArrayInputStream(baos.toByteArray());

        File file;
        try {
            file = fileService.prepareFileForPrediction(predictionInputStream);
        } catch (IOException e) {
            log.error("Receiving file from user failed", e);
            return;
        }

        var response = modelApi.predictImageBinary(file, selectedModel);
        updatePredictionLayout(response);
        file.delete();

        predImage.setSrc(new StreamResource("image", () -> new DataInputStream(imageInputStream)));
        singleFileUpload.clearFileList();
    }

    private void updatePredictionLayout(Prediction prediction) {
        var labelResult = new Label();
        if (prediction == null) {
            labelResult.setText("Prześlij plik, aby rozpoznać element.");
            labelResult.getElement().getStyle().set("font-size", "30px");
        } else {
            boolean isPositive = prediction.getScore() >= 0.5;
            long percentage = Math.round(prediction.getScore() * 100);
            String divText;
            if (isPositive) {
                divText = String.format("Jest to %s. Prawdopodobieństwo: %d%%", prediction.getLabel().toLowerCase(), percentage);
                labelResult.getElement().getStyle().set("color", "green");
            } else {
                labelResult.getElement().getStyle().set("font-size", "30px");
                divText = String.format("Nie jest to %s. Prawdopodobieństwo: (%d%%)", prediction.getLabel().toLowerCase(), percentage);
                labelResult.getElement().getStyle().set("color", "red");

            }
            labelResult.setText(divText);
        }

        labelResult.getElement().getStyle().set("font-size", "30px");
        predictionLayout.removeAll();
        predictionLayout.add(labelResult);
        predictionLayout.setAlignItems(Alignment.CENTER);
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String modelName) {
        selectedModel = modelName;
    }
}

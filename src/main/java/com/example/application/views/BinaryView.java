package com.example.application.views;

import com.example.application.service.FileService;
import com.example.application.service.ModelApi;
import com.example.application.views.util.ComponentUtil;
import com.example.application.views.util.ResultHolderBinary;
import com.example.application.views.util.UploadPolishI18N;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@PageTitle("Klasyfikacja binarna")
@Route(value = "binarna", layout = MainLayout.class)
@RouteAlias(value = "binarna", layout = MainLayout.class)
@Slf4j
public class BinaryView extends VerticalLayout implements HasUrlParameter<String> {

    private final ModelApi modelApi;
    private final FileService fileService;
    private Upload multiFileUpload;
    Scroller scroller = new Scroller();
    private final HorizontalLayout predictionLayout = new HorizontalLayout();
    MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
    private final List<ResultHolderBinary> results = new ArrayList<>();
    private String selectedModel;
    private Button resetBtn;
    private final ComponentUtil componentUtil = new ComponentUtil();

    public BinaryView(ModelApi modelApi, FileService fileService) {
        this.modelApi = modelApi;
        this.fileService = fileService;
        init();
    }

    private void init() {
        var uploadComponent = initUploadComponent();
        updatePredictionLayout();

        predictionLayout.setPadding(true);
        predictionLayout.getStyle().set("display", "inline-flex");

        scroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);
        scroller.setContent(predictionLayout);

        add(uploadComponent, scroller);
        uploadComponent.setHeight("15%");
        scroller.setHeight("85%");
        setHeight("100%");
    }

    private HorizontalLayout initUploadComponent() {
        resetBtn = new Button("Wyczyść");
        resetBtn.addClickListener(e -> {
                    results.clear();
                    updatePredictionLayout();
                }
        );

        multiFileUpload = new Upload(multiFileMemoryBuffer);
        multiFileUpload.setI18n(new UploadPolishI18N());
        multiFileUpload.addSucceededListener(this::processAndAddFile);
        multiFileUpload.addAllFinishedListener(event -> updatePredictionLayout());

        var uploadComponent = new HorizontalLayout(resetBtn, multiFileUpload);
        resetBtn.setWidth("10%");
        resetBtn.setHeight("100%");
        multiFileUpload.setWidth("90%");
        multiFileUpload.setHeight("100%");
        uploadComponent.setAlignItems(Alignment.CENTER);
        uploadComponent.setWidth("100%");
        return uploadComponent;
    }

    @SneakyThrows
    private void processAndAddFile(SucceededEvent event) {
        String fileName = event.getFileName();
        var baos = new ByteArrayOutputStream();
        multiFileMemoryBuffer.getInputStream(fileName).transferTo(baos);

        InputStream predictionInputStream = new ByteArrayInputStream(baos.toByteArray());
        File file;
        try {
            file = fileService.prepareFileForPrediction(predictionInputStream);
        } catch (IOException e) {
            log.error("Receiving file from user failed", e);
            return;
        }

        var response = modelApi.predictImageBinary(file, selectedModel);
        results.add(new ResultHolderBinary(response, baos));
        file.delete();
    }

    private void updatePredictionLayout() {
        multiFileUpload.clearFileList();
        if (results.isEmpty()) {
            predictionLayout.removeAll();
        } else {
            var components = results.stream()
                    .map(result -> {
                        return componentUtil.getOriginalImageWithTitleAndPercentage(result.getImage(), selectedModel, result.getPrediction().getScore());
                    }).collect(Collectors.toList());
            predictionLayout.removeAll();
            components.forEach(predictionLayout::add);
        }
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String modelName) {
        selectedModel = modelName;
    }
}

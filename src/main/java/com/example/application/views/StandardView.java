package com.example.application.views;

import com.example.application.dto.ModelPrediction;
import com.example.application.dto.Prediction;
import com.example.application.service.FileService;
import com.example.application.service.ModelApi;
import com.example.application.views.util.ComponentUtil;
import com.example.application.views.util.ResultHolder;
import com.example.application.views.util.UploadPolishI18N;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Model standardowy")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Slf4j
public class StandardView extends VerticalLayout {

    private final ModelApi modelApi;
    private final FileService fileService;
    private Grid<Prediction> predictionGrid;
    private final HorizontalLayout predictionLayout = new HorizontalLayout();
    private Upload multiFileUpload;
    MultiFileMemoryBuffer multiFileMemoryBuffer = new MultiFileMemoryBuffer();
    private Image predImage;
    private final ComponentUtil componentUtil = new ComponentUtil();
    private Button backBtn;
    private Label indexStatus;
    private Button nextBtn;
    private Button resetBtn;

    private final List<ResultHolder> results = new ArrayList<>();
    private int currentIndex = 0;

    public StandardView(ModelApi modelApi, FileService fileService) {
        this.modelApi = modelApi;
        this.fileService = fileService;
        init();
    }

    private void init() {
        var uploadComponent = initUploadComponent();
        updatePredictionLayout(null);

        var topLayout = new HorizontalLayout(uploadComponent, predictionLayout);
        uploadComponent.setWidth("35%");
        predictionLayout.setWidth("65%");

        predictionGrid = new Grid<>(Prediction.class, false);
        predictionGrid.addColumn(Prediction::getLabel).setHeader("Klasa");
        predictionGrid.addColumn(prediction -> String.format("%.4f", prediction.getScore())).setHeader("Prawdopodobieństwo");

        add(topLayout, predictionGrid);
        topLayout.setHeight("70%");
        topLayout.setWidth("100%");
        predictionGrid.setHeight("30%");
        setHeight("100%");
    }

    private VerticalLayout initUploadComponent() {
        backBtn = new Button("Poprzednie");
        backBtn.addClickListener(e -> {
                    if (!results.isEmpty() && currentIndex > 0) {
                        currentIndex -= 1;
                        displayIndex(currentIndex);
                    }
                }
        );

        indexStatus = new Label("0/0");

        nextBtn = new Button("Następne");
        nextBtn.addClickListener(e -> {
                    if (!results.isEmpty() && currentIndex < results.size() - 1) {
                        currentIndex += 1;
                        displayIndex(currentIndex);
                    }
                }
        );

        resetBtn = new Button("Wyczyść");
        resetBtn.addClickListener(e -> {
                    results.clear();
                    currentIndex = 0;
                    updatePredictionLayout(null);
                    predictionGrid.setItems(List.of());
                    indexStatus.setText("0/0");
                    predImage.setSrc("");
                }
        );

        var naviArrows = new HorizontalLayout(backBtn, indexStatus, nextBtn, resetBtn);
        naviArrows.setAlignItems(Alignment.CENTER);

        multiFileUpload = new Upload(multiFileMemoryBuffer);
        multiFileUpload.setI18n(new UploadPolishI18N());
        multiFileUpload.addSucceededListener(this::processAndAddFile);
        multiFileUpload.addAllFinishedListener(event -> displayIndex(0));
        multiFileUpload.setWidth(100, Unit.PERCENTAGE);
        multiFileUpload.setHeight(30, Unit.PERCENTAGE);

        predImage = new Image();
        predImage.setMaxWidth("100%");
        predImage.setMaxHeight("70%");

        var uploadComponent = new VerticalLayout(naviArrows, multiFileUpload, predImage);
        uploadComponent.setHorizontalComponentAlignment(Alignment.CENTER, predImage);
        return uploadComponent;
    }

    private void updatePredictionLayout(ModelPrediction modelPrediction) {
        if (modelPrediction == null) {
            var ceownikImg = componentUtil.getImageWithTitleAndPercentage("/images/ceownik.png", "Ceownik", 0d, false);
            var dwuteownikImg = componentUtil.getImageWithTitleAndPercentage("/images/dwuteownik.png", "Dwuteownik", 0d, false);
            var katownikImg = componentUtil.getImageWithTitleAndPercentage("/images/katownik.png", "Kątownik", 0d, false);
            var kwadratowyImg = componentUtil.getImageWithTitleAndPercentage("/images/kwadratowy.png", "Kwadratowy", 0d, false);
            var okraglyImg = componentUtil.getImageWithTitleAndPercentage("/images/okragly.png", "Okrągły", 0d, false);
            var plaskownikImg = componentUtil.getImageWithTitleAndPercentage("/images/plaskownik.png", "Płaskownik", 0d, false);
            var profilImg = componentUtil.getImageWithTitleAndPercentage("/images/profil.png", "Profil", 0d, false);
            var ruraImg = componentUtil.getImageWithTitleAndPercentage("/images/rura.png", "Rura", 0d, false);

            var vertical1 = new VerticalLayout(ceownikImg, dwuteownikImg);
            var vertical2 = new VerticalLayout(katownikImg, plaskownikImg);
            var vertical3 = new VerticalLayout(kwadratowyImg, okraglyImg);
            var vertical4 = new VerticalLayout(profilImg, ruraImg);

            predictionLayout.removeAll();
            predictionLayout.add(vertical1, vertical2, vertical3, vertical4);
        } else {
            var components = modelPrediction.getPredictions().stream()
                    .limit(2)
                    .filter(prediction -> prediction.getScore() >= 0.1)
                    .map(prediction -> {
                        switch (prediction.getLabel()) {
                            case "ceownik":
                                return componentUtil.getImageWithTitleAndPercentage("/images/ceownik.png", "Ceownik", prediction.getScore(), true);
                            case "dwuteownik":
                                return componentUtil.getImageWithTitleAndPercentage("/images/dwuteownik.png", "Dwuteownik", prediction.getScore(), true);
                            case "katownik":
                                return componentUtil.getImageWithTitleAndPercentage("/images/katownik.png", "Kątownik", prediction.getScore(), true);
                            case "kwadratowy":
                                return componentUtil.getImageWithTitleAndPercentage("/images/kwadratowy.png", "Kwadratowy", prediction.getScore(), true);
                            case "okragly":
                                return componentUtil.getImageWithTitleAndPercentage("/images/okragly.png", "Okrągły", prediction.getScore(), true);
                            case "plaskownik":
                                return componentUtil.getImageWithTitleAndPercentage("/images/plaskownik.png", "Płaskownik", prediction.getScore(), true);
                            case "profil":
                                return componentUtil.getImageWithTitleAndPercentage("/images/profil.png", "Profil", prediction.getScore(), true);
                            case "rura":
                                return componentUtil.getImageWithTitleAndPercentage("/images/rura.png", "Rura", prediction.getScore(), true);
                            default:
                                return null;
                        }
                    }).collect(Collectors.toList());
            predictionLayout.removeAll();
            components.forEach(predictionLayout::add);
        }
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

        var response = modelApi.predictImageStandard(file);
        results.add(new ResultHolder(response, baos));
        file.delete();
    }

    private void displayIndex(int index) {
        indexStatus.setText(currentIndex + 1 + "/" + results.size());
        updatePredictionLayout(results.get(index).getPrediction());

        InputStream imageInputStream = new ByteArrayInputStream(results.get(index).getImage().toByteArray());
        predImage.setSrc(new StreamResource("image", () -> new DataInputStream(imageInputStream)));
        predictionGrid.setItems(results.get(index).getPrediction().getPredictions());
        multiFileUpload.clearFileList();
    }
}

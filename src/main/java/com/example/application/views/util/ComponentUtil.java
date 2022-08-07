package com.example.application.views.util;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.server.StreamResource;

public class ComponentUtil {

    public VerticalLayout getImageWithTitleAndPercentage(String imagePath, String title, Double score, boolean big) {
        var imageWithTitle = new VerticalLayout();

        var imageResource = new StreamResource(title,
                () -> getClass().getResourceAsStream(imagePath));
        var image = new Image(imageResource, title);

        image.getStyle().set("border", "2px solid #1C6EA4");
        image.getStyle().set("border-radius", "6px");
        image.setSizeFull();

        ProgressBar progressBar = new ProgressBar();
        progressBar.setHeight(10, Unit.PIXELS);
        progressBar.setValue(score);

        Div progressBarLabel = new Div();
        long percentage = Math.round(score * 100);
        progressBarLabel.setText(String.format("%s (%d%%)", title, percentage));
        imageWithTitle.add(progressBarLabel, progressBar, image);

        imageWithTitle.setSpacing(false);
        imageWithTitle.setMargin(false);
        imageWithTitle.setAlignItems(FlexComponent.Alignment.CENTER);

        if (big) {
            imageWithTitle.setWidth("48%");
        } else {
            imageWithTitle.setWidth("100%");
        }
        return imageWithTitle;
    }
}

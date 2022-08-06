package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("O aplikacji")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(false);

        Image img = new Image("images/info-image.png", "placeholder");
        img.setWidth("30%");
        add(img);

        add(new H2("Aplikacja internetowa powstała jako część pracy dyplomowej pod tytułem\n\"System do rozpoznawania i klasyfikowania wybranych kategorii kształtów 2D i 3D\""));
        add(new Paragraph("Autor: Krystian Nowakowski"));

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}

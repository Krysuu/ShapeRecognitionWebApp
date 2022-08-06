package com.example.application.views;


import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.views.about.AboutView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.router.PageTitle;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Rozpoznawanie elementów konstrukcyjnych");
        appName.addClassNames("app-name");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private AppNav createNavigation() {
        AppNav nav = new AppNav();
        nav.addClassNames("app-nav");

        nav.addItem(new AppNavItem("Model standardowy", StandardView.class, "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - ceownik","binarna/ceownik", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - dwuteownik","binarna/dwuteownik", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - kątownik","binarna/katownik", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - pręt kwadratowy","binarna/kwadratowy", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - pręt okrągły","binarna/okragly", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - płaskownik","binarna/plaskownik", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - profil","binarna/profil", "la la-globe"));
        nav.addItem(new AppNavItem("Model binarny - rura","binarna/rura", "la la-globe"));
        nav.addItem(new AppNavItem("O aplikacji", AboutView.class, "la la-file"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("app-nav-footer");

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}

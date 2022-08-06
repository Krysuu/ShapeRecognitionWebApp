package com.example.application.views;

import lombok.Getter;

@Getter
public enum PredictClassNames {
    CEOWNIK("Ceownik", "ceownik"),
    DWUTEOWNIK("Dwuteownik", "dwuteownik"),
    KATOWNIK("Kątownik", "katownik"),
    KWADRATOWY("Pręt kwadratowy", "kwadratowy"),
    OKRAGLY("Pręt okrągły", "okragly"),
    PLASKOWNIK("Plaskownik", "plaskownik"),
    PROFIL("Profil", "profil"),
    RURA("Rura", "rura");

    String prettyName;
    String technical;

    PredictClassNames(String prettyName, String technical) {
        this.prettyName = prettyName;
        this.technical = technical;
    }

    public PredictClassNames getEnumByTechnical(String type) {
        for (PredictClassNames names : values()) {
            if (names.getTechnical().equals(type)) {
                return names;
            }
        }
        return null;
    }
}

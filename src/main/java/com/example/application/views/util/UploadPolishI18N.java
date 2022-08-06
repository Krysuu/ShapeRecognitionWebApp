package com.example.application.views.util;

import com.vaadin.flow.component.upload.UploadI18N;

import java.util.Arrays;

public class UploadPolishI18N extends UploadI18N {
    public UploadPolishI18N() {
        setDropFiles(new DropFiles()
                .setOne("albo przeciągnij i upuść tutaj")
                .setMany("albo przeciągnij i upuść tutaj"));
        setAddFiles(new AddFiles()
                .setOne("Wybierz plik")
                .setMany("Wybierz pliki"));
        setCancel("Anuluj");
        setError(new Error()
                .setTooManyFiles("Za dużo plików.")
                .setFileIsTooBig("Plik jest za duży.")
                .setIncorrectFileType("Nieprawidłowy format pliku."));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status()
                        .setConnecting("Łączenie...")
                        .setStalled("Zatrzymano")
                        .setProcessing("Trwa przetwarzanie pliku...")
                        .setHeld("Wstrzymano"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("Pozostały czas: ")
                        .setUnknown("Pozostały czas nieoszacowany"))
                .setError(new Uploading.Error()
                        .setServerUnavailable("Serwer nie odpowiada")
                        .setUnexpectedServerError("Błąd serwera")
                        .setForbidden("Zabroniony")));
        setUnits(new Units()
                .setSize(Arrays.asList("B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")));
    }
}

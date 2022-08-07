package com.example.application.service;

import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileService {
    private static final String PREFIX = "predict";
    private static final String SUFFIX = ".tmp";
    private static final int TARGET_WIDTH = 224;
    private static final int TARGET_HEIGHT = 224;

    @Value("#{new Boolean('${model.resize-images}')}")
    private Boolean resizeImages;

    public File prepareFileForPrediction(InputStream inputStream) throws IOException {
        var file = inputStreamToFile(inputStream);
        if (resizeImages) {
            resizeFileImage(file);
        }
        return file;
    }

    private File inputStreamToFile(InputStream inputStream) throws IOException {
        var tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();
        try (var out = new FileOutputStream(tempFile)) {
            IOUtils.copy(inputStream, out);
        }
        return tempFile;
    }

    private void resizeFileImage(File file) throws IOException {
        var bufferedImage = ImageIO.read(file);
        bufferedImage = Scalr.resize(bufferedImage, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_EXACT, TARGET_WIDTH, TARGET_HEIGHT);
        ImageIO.write(bufferedImage, "jpg", file);
    }
}

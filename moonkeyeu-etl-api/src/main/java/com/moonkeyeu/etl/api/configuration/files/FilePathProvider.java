package com.moonkeyeu.etl.api.configuration.files;

import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@Component
public class FilePathProvider {

    private final RootConfig rootConfig;

    public FilePathProvider(RootConfig rootConfig) {
        this.rootConfig = rootConfig;
    }

    public String getJsonSource(String segment, String filename) {
        return Paths.get(rootConfig.getJsonRootFolder(), segment, filename).toString();
    }

    public String getJsonDir(String segment) {
        return Paths.get(rootConfig.getJsonRootFolder(), segment).toString();
    }

    public String getImagesDir(String dirName) {
        return Paths.get(rootConfig.getImagesRootFolder(), dirName).toString();
    }
}

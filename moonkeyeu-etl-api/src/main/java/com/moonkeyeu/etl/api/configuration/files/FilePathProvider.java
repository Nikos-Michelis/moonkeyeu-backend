package com.moonkeyeu.etl.api.configuration.files;

import com.moonkeyeu.etl.api.dto.EntityConfig;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

@Component
public class FilePathProvider {
    private final RootConfig rootConfig;

    public FilePathProvider(RootConfig rootConfig) {
        this.rootConfig = rootConfig;
    }

    public String getJsonSource(String filename) {
        return Paths.get(rootConfig.getJsonRootFolder(), filename).toString();
    }

    public String getCsvSource(String filename) {
        return Paths.get(rootConfig.getCsvRootFolder(), filename).toString();
    }

    public String getImagesDir(String dirName) {
        return Paths.get(rootConfig.getImagesRootFolder(), dirName).toString();
    }

    public LinkedList<EntityConfig> getCsvGroups() {
        return Arrays.stream(CsvGroup.values())
                .sorted(Comparator.comparingInt(CsvGroup::getOrder))
                .map(g -> new EntityConfig(getCsvSource(g.getCsvSource().getCsvFile()), g.getEntityClass(), g.getOrder()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

}

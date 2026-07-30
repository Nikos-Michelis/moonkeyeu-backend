package com.moonkeyeu.etl.api.configuration.files;

import com.moonkeyeu.etl.api.dto.storage.EntityConfig;
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

    public String getJsonSource(String segment, String filename) {
        return Paths.get(rootConfig.getJsonRootFolder(), segment, filename).toString();
    }

    public String getCsvSource(String filename) {
        return Paths.get(rootConfig.getCsvRootFolder(), filename).toString();
    }

    public String getJsonDir(String segment) {
        return Paths.get(rootConfig.getJsonRootFolder(), segment).toString();
    }

    public String getImagesDir(String dirName) {
        return Paths.get(rootConfig.getImagesRootFolder(), dirName).toString();
    }

    public LinkedList<EntityConfig> getCsvGroups() {
        return Arrays.stream(CsvGroup.values())
                .sorted(Comparator.comparingInt(CsvGroup::getOrder))
                .map(group -> new EntityConfig(getCsvSource(group.getCsvSource().getCsvFile()), group.getEntityClass(), group.getOrder()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

}

package com.moonkeyeu.etl.api.configuration.files;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RootConfig {
    @Value("${application.path.root.data.folder.json}")
    private String jsonRootFolder;
    @Value("${application.path.root.data.folder.images}")
    private String imagesRootFolder;
}

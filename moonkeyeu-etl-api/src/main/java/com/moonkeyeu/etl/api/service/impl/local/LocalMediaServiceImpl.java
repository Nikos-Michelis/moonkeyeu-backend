package com.moonkeyeu.etl.api.service.impl.local;

import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.model.images.*;
import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import com.moonkeyeu.etl.api.model.media.MissionPatchesEntity;
import com.moonkeyeu.etl.api.model.pad.LaunchPadEntity;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.LocalStorageService;
import com.moonkeyeu.etl.api.service.MediaDownloadService;
import com.moonkeyeu.etl.api.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalMediaServiceImpl implements LocalMediaService {

    private final LocalStorageService localStorageService;
    private final MediaDownloadService mediaDownloadService;
    @Value("${application.backend.url}")
    private String localHostUrl;

    private final Map<Class<? extends CsvEntity<?>>, String> entityToLocalKeyMap = Map.of(
            RocketImageEntity.class, "rockets",
	        PadImagesEntity.class, "pads",
            LaunchPadEntity.class, "pads-locations",
            LauncherImagesEntity.class, "launchers",
            SpacecraftImagesEntity.class, "spacecraft",
            AstronautImagesEntity.class, "astronauts",
            AgenciesImagesEntity.class, "agencies",
            MissionPatchesEntity.class, "missions-patches",
            ProgramsImagesEntity.class, "programs"
    );

    public String saveMediaLocal(ImageEntity item, String localDir) throws IOException {
        String basePath = entityToLocalKeyMap.get(item.getClass());
        if (basePath == null) {
            throw new IllegalStateException(
                    "No S3 path mapping for entity: " + item.getClass().getSimpleName()
            );
        }

        String fileName = ClientUtils.extractImageNameFromURL(item.getImageUrl());
        Path filePath = Paths.get(localDir, fileName);

        String localStorageUrl =
                UriComponentsBuilder
                        .fromUriString(localHostUrl)
                        .pathSegment(basePath)
                        .pathSegment(fileName)
                        .toUriString();


        if (localStorageService.existsByKey(filePath)) {
            return localStorageUrl;
        }

        Files.createDirectories(Paths.get(localDir));
        byte[] data = mediaDownloadService.download(item.getImageUrl());
        localStorageService.save(data, filePath);
        return localStorageUrl;

    }
}

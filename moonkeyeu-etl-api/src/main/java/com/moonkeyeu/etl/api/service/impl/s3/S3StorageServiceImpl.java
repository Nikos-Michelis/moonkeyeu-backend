package com.moonkeyeu.etl.api.service.impl.s3;

import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.model.images.*;
import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import com.moonkeyeu.etl.api.model.media.MissionPatchesEntity;
import com.moonkeyeu.etl.api.model.pad.LaunchPadEntity;
import com.moonkeyeu.etl.api.service.ClientS3CloudService;
import com.moonkeyeu.etl.api.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3StorageServiceImpl implements S3StorageService {

    @Value("${aws.s3.buckets.root}")
    private String s3KeyValue;
    private final ClientS3CloudService clientS3CloudService;

    private final Map<Class<?>, String> entityToS3KeyMap = Map.of(
            RocketImageEntity.class, "/rockets/",
            LaunchPadEntity.class, "/pads-locations/",
            LauncherImagesEntity.class, "/launchers/",
            SpacecraftImagesEntity.class, "/spacecraft/",
            AstronautImagesEntity.class, "/astronauts/",
            AgenciesImagesEntity.class, "/agencies/",
            MissionPatchesEntity.class, "/missions-patches/",
            ProgramsImagesEntity.class, "/programs/"
    );

    @Override
    public void saveMediaToS3(CsvEntity<?> item, String bucketName, boolean skipUpload) {
        if (item instanceof ImageEntity) {
            setImageUrl((ImageEntity) item, bucketName, skipUpload);
        }
    }

    @Override
    public void setImageUrl(ImageEntity entity, String bucketName, boolean skipUpload) {
        try {
            String s3Key = entityToS3KeyMap.get(entity.getClass()) != null ? s3KeyValue + entityToS3KeyMap.get(entity.getClass()) : null;
            if (s3Key != null) {
                String imageUrl = clientS3CloudService.saveToS3(entity.getImageUrl(), bucketName, s3Key, skipUpload);
                entity.setImageUrl(imageUrl);
            }
        } catch (IOException  e) {
            log.error("Error processing batch: ", e);
        }
    }
}

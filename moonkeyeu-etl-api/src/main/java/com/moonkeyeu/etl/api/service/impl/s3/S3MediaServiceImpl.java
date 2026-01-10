package com.moonkeyeu.etl.api.service.impl.s3;

import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.model.images.*;
import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import com.moonkeyeu.etl.api.model.media.MissionPatchesEntity;
import com.moonkeyeu.etl.api.model.pad.LaunchPadEntity;
import com.moonkeyeu.etl.api.service.impl.MediaDownloadServiceImpl;
import com.moonkeyeu.etl.api.service.S3StorageService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3MediaServiceImpl implements S3MediaService {

    @Value("${aws.s3.buckets.root}")
    private String s3KeyValue;
    @Value("${aws.cloudfront.url}")
    private String cloudFrontUrl;
    private final S3StorageService s3StorageService;
    private final MediaDownloadServiceImpl mediaDownloadService;

    private final Map<Class<?>, String> entityToS3KeyMap = Map.of(
            RocketImageEntity.class, "rockets",
            LaunchPadEntity.class, "pads-locations",
            LauncherImagesEntity.class, "launchers",
            SpacecraftImagesEntity.class, "spacecraft",
            AstronautImagesEntity.class, "astronauts",
            AgenciesImagesEntity.class, "agencies",
            MissionPatchesEntity.class, "missions-patches",
            ProgramsImagesEntity.class, "programs"
    );

    @Override
    public String saveMediaToS3(ImageEntity item, String bucketName, boolean skipUpload) throws IOException {
        String basePath = entityToS3KeyMap.get(item.getClass());
        if (basePath == null) {
            throw new IllegalStateException(
                    "No S3 path mapping for entity: " + item.getClass().getSimpleName()
            );
        }

        String fileName = ClientUtils.extractImageNameFromURL(item.getImageUrl());

        String s3Key =
                UriComponentsBuilder
                        .fromPath(s3KeyValue)
                        .pathSegment(basePath)
                        .pathSegment(fileName)
                        .toUriString();

        String cloudFrontUrl =
                UriComponentsBuilder
                        .fromUriString(this.cloudFrontUrl)
                        .pathSegment(s3KeyValue)
                        .pathSegment(basePath)
                        .pathSegment(fileName)
                        .toUriString();


        if (skipUpload) {
            return cloudFrontUrl;
        }

        if (s3StorageService.existsByKey(s3Key, bucketName)) {
            return cloudFrontUrl;
        }

        byte[] data = mediaDownloadService.download(item.getImageUrl());
        s3StorageService.save(data, s3Key, bucketName);
        return cloudFrontUrl;
    }
}

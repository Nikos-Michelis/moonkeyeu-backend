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
import java.net.MalformedURLException;
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
            LaunchImagesEntity.class, "launch",
            LaunchPadEntity.class, "pads-locations",
            LauncherImagesEntity.class, "launchers",
            SpacecraftImagesEntity.class, "spacecraft",
            AstronautImagesEntity.class, "astronauts",
            AgenciesImagesEntity.class, "agencies",
            MissionPatchesEntity.class, "missions-patches",
            ProgramsImagesEntity.class, "programs"
    );

    @Override
    public String saveMediaToS3(ImageEntity imageEntity, String bucketName) throws IOException {
        String s3Key = getS3Key(imageEntity);
        String cloudFrontUrl = getCloudFrontUrl(imageEntity);

        if (s3StorageService.existsByKey(s3Key, bucketName)) {
            return cloudFrontUrl;
        }

        byte[] data = mediaDownloadService.download(imageEntity.getImageUrl());
        s3StorageService.save(data, s3Key, bucketName);
        return cloudFrontUrl;
    }

    public String getCloudFrontUrl(ImageEntity imageEntity) throws MalformedURLException {
        String basePath = getRootPath(imageEntity);
        String fileName = getFileName(imageEntity);
        return UriComponentsBuilder
                .fromUriString(this.cloudFrontUrl)
                .pathSegment(s3KeyValue)
                .pathSegment(basePath)
                .pathSegment(fileName)
                .toUriString();
    }

    private String getS3Key(ImageEntity imageEntity) throws MalformedURLException {
        String basePath = getRootPath(imageEntity);
        String fileName = getFileName(imageEntity);
        return String.format("%s/%s/%s", s3KeyValue, basePath, fileName);
    }

    private String getFileName(ImageEntity item) throws MalformedURLException {
        return ClientUtils.extractImageNameFromURL(item.getImageUrl());
    }

    private String getRootPath(ImageEntity item) {
        return entityToS3KeyMap.get(item.getClass());
    }
}

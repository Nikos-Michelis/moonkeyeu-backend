package com.moonkeyeu.etl.api.service.impl.s3;

import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import com.moonkeyeu.etl.api.service.MediaDownloadService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.service.S3StorageService;
import com.moonkeyeu.etl.api.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3MediaServiceImpl implements S3MediaService {

    @Value("${aws.s3.buckets.root}")
    private String s3KeyValue;
    @Value("${aws.cloudfront.url}")
    private String cloudFrontUrl;

    private final S3StorageService s3StorageService;
    private final MediaDownloadService mediaDownloadService;

    @Override
    public String saveMediaToS3(StorableImage image, String bucketName) throws IOException {
        String s3Key = getS3Key(image);
        String url = getCloudFrontUrl(image);

        if (s3StorageService.existsByKey(s3Key, bucketName)) {
            return url;
        }

        byte[] data = mediaDownloadService.download(image.getImageUrl());
        s3StorageService.save(data, s3Key, bucketName);
        return url;
    }

    @Override
    public String getCloudFrontUrl(StorableImage image) throws MalformedURLException {
        return UriComponentsBuilder
                .fromUriString(this.cloudFrontUrl)
                .path(s3KeyValue)
                .pathSegment(image.getFolder(), getFileName(image))
                .toUriString();
    }

    @Override
    public String getBaseUrl() {
        return cloudFrontUrl;
    }

    private String getS3Key(StorableImage image) throws MalformedURLException {
        return String.format("%s/%s/%s", s3KeyValue, image.getFolder(), getFileName(image));
    }

    private String getFileName(StorableImage image) throws MalformedURLException {
        return ClientUtils.extractImageNameFromURL(image.getImageUrl());
    }
}

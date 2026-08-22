package com.moonkeyeu.etl.api.service.impl.local;

import com.moonkeyeu.etl.api.pipeline.ll2.media.StorableImage;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.LocalStorageService;
import com.moonkeyeu.etl.api.service.MediaDownloadService;
import com.moonkeyeu.etl.api.settings.exceptions.LocalStorageException;
import com.moonkeyeu.etl.api.utils.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalMediaServiceImpl implements LocalMediaService {

    private final LocalStorageService localStorageService;
    private final MediaDownloadService mediaDownloadService;

    @Value("${application.backend.url}")
    private String localHostUrl;

    @Override
    public String saveMediaLocal(StorableImage media, String localDir) throws IOException {

        if (media == null) {
            throw new LocalStorageException("Media entity should not be null or empty");
        }

        String fileName = getFileName(media);
        Path directory = Paths.get(localDir, media.getFolder());
        Path filePath = directory.resolve(fileName);
        String localStorageUrl = getLocalHostUrl(media);

        if (localStorageService.existsByKey(filePath)) {
            return localStorageUrl;
        }

        Files.createDirectories(directory);
        byte[] data = mediaDownloadService.download(media.getImageUrl());
        localStorageService.save(data, filePath);
        return localStorageUrl;
    }

    @Override
    public String getLocalHostUrl(StorableImage media) throws MalformedURLException {
        return UriComponentsBuilder
                .fromUriString(this.localHostUrl)
                .pathSegment(media.getFolder())
                .pathSegment(getFileName(media))
                .toUriString();
    }

    @Override
    public String getBaseUrl() {
        return localHostUrl;
    }

    private String getFileName(StorableImage media) throws MalformedURLException {
        return ClientUtils.extractImageNameFromURL(media.getImageUrl());
    }
}

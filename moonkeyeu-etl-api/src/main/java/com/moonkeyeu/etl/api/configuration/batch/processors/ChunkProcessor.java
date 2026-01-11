package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class ChunkProcessor implements ItemProcessor<CsvEntity<?>, CsvEntity<?>> {
    private static final String UNKNOWN_VALUE = "Unknown";
    private final LocalMediaService localMediaService;
    private final S3MediaService s3MediaService;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;
    private final S3Buckets s3Buckets;
    @Value("#{jobParameters['s3StorageEnabled'] ?: 'false'}")
    private boolean s3StorageEnabled;
    @Value("#{jobParameters['localStorageEnabled'] ?: 'false'}")
    private boolean localStorageEnabled;

    @Override
    public CsvEntity<?> process(CsvEntity item) throws IOException {
        if (item.getPrimaryKey() == null) return null;

        cleanValuesByField(item);

        if (item instanceof ImageEntity entity) {
            String imageUrl = mediaStorageProvider(entity);
            entity.setImageUrl(imageUrl);
        }

        return item;
    }

    private String mediaStorageProvider(ImageEntity entity) throws IOException {

        if (localStorageEnabled) {
            return localMediaService.saveMediaLocal(entity, filePathProvider.getImagesDir(rootConfig.getImagesRootFolder()));
        }

        if (s3StorageEnabled) {
            return s3MediaService.saveMediaToS3(entity, s3Buckets.getBucketName(), s3StorageEnabled);
        }

        throw new InvalidStoreProviderException("Unknown media store method.");
    }

    private void cleanValuesByField(CsvEntity<?> item) {
        for (Field field : item.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object originalValue = field.get(item);
                Object cleanedByNullOrEmpty = handleNullEmptyValues(originalValue);
                Object cleaned = handleSpecialCharacters(cleanedByNullOrEmpty);
                field.set(item, cleaned);
            } catch (IllegalAccessException e) {
                log.error("Error cleaning field '{}' for entity {}",
                        field.getName(), item.getClass().getSimpleName(), e);
                throw new RuntimeException("Error cleaning data", e);
            }
        }
    }

    private Object handleSpecialCharacters(Object value) {
        if (value == null) {
            return null;
        }
        return "???".equals(value) ? UNKNOWN_VALUE : value;
    }

    private Object handleNullEmptyValues(Object value) {
        if (value instanceof String str) {
            str = str.trim();
            return str.isEmpty() ? null : str;
        }
        return value;
    }
}

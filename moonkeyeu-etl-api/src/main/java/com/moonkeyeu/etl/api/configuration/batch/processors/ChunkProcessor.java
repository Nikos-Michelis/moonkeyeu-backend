package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.service.s3.S3StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class ChunkProcessor implements ItemProcessor<CsvEntity<?>, CsvEntity<?>> {
    private static final String UNKNOWN_VALUE = "Unknown";
    private final S3StorageService s3StorageService;
    private final S3Buckets s3Buckets;
    @Value("#{jobParameters['skipS3BucketUpload'] ?: 'false'}")
    private boolean skipS3BucketUpload;

    @Override
    public CsvEntity<?> process(CsvEntity item) {
        //System.out.println(item);
        if (item.getPrimaryKey() == null) return null;
        cleanValuesByField(item);
        s3StorageService.saveMediaToS3(item, s3Buckets.getBucketName(), skipS3BucketUpload);
        return item;
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

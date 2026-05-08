package com.moonkeyeu.etl.api.configuration.batch.processors;

import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.dto.storage.StoreOperation;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.ImageEntity;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreOperationException;
import com.moonkeyeu.etl.api.settings.exceptions.InvalidStoreProviderException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@StepScope
@Setter
public class MediaProcessor implements ItemProcessor<CsvEntity<?>, CsvEntity<?>> {
    private final LocalMediaService localMediaService;
    private final S3MediaService s3MediaService;
    private final RootConfig rootConfig;
    private final FilePathProvider filePathProvider;
    private final S3Buckets s3Buckets;
    @Value("#{jobParameters['storage'] ?: null}")
    private String storage;
    @Value("#{jobParameters['operation'] ?: null}")
    private String operation;


    @Override
    public CsvEntity<?> process(CsvEntity item) throws IOException {

        if (item.getPrimaryKey() == null) return null;

        if (item instanceof ImageEntity entity) {
            String imageUrl = mediaStorageProvider(entity);
            entity.setImageUrl(imageUrl);
        }

        return item;
    }

    private String mediaStorageProvider(ImageEntity entity) throws InvalidStoreProviderException, IOException {

        if (storage == null || storage.isEmpty()) {
            throw new InvalidStoreProviderException("storage is null");
        }

        StorageType storageType = StorageType.from(storage);
        return switch (storageType) {
            case LOCAL_STORAGE -> saveToLocal(entity);
            case S3_STORAGE -> saveToS3(entity);
        };
    }

    private String saveToLocal(ImageEntity entity) throws InvalidStoreOperationException, IOException {

        if (storage == null) {
            throw new InvalidStoreOperationException("operation is null");
        }

        StoreOperation storeOperation = StoreOperation.from(operation);
        return switch (storeOperation) {
            case LOCAL_SAVE ->
                    localMediaService.saveMediaLocal(entity, filePathProvider.getImagesDir(rootConfig.getImagesRootFolder()));
            case GET_URL ->
                    localMediaService.getLocalHostUrl(entity);
            default -> throw new InvalidStoreOperationException("Unexpected operation: " + storeOperation);
        };
    }

    private String saveToS3(ImageEntity entity) throws InvalidStoreOperationException, IOException {

        if (storage == null) {
            throw new InvalidStoreOperationException("operation is null");
        }

        StoreOperation storeOperation = StoreOperation.from(operation);
        return switch (storeOperation) {
            case S3_UPLOAD ->
                    s3MediaService.saveMediaToS3(entity, s3Buckets.getBucketName());
            case GET_URL ->
                    s3MediaService.getCloudFrontUrl(entity);
            default -> throw new InvalidStoreOperationException("Unexpected operation: " + storeOperation);
        };
    }
}

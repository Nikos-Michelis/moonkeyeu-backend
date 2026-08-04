package com.moonkeyeu.etl.api.configuration.storage;

import com.moonkeyeu.etl.api.configuration.files.FilePathProvider;
import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.dto.storage.StorageType;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.service.strategy.LocalStorageStrategy;
import com.moonkeyeu.etl.api.service.strategy.S3StorageStrategy;
import com.moonkeyeu.etl.api.service.strategy.StorageStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageStrategyConfig {
    
    @Bean(StorageType.S3_STORAGE)
    public StorageStrategy s3Strategy(S3MediaService s3MediaService, S3Buckets s3Buckets) {
        return new S3StorageStrategy(s3MediaService, s3Buckets);
    }

    @Bean(StorageType.LOCAL_STORAGE)
    public StorageStrategy localStrategy(LocalMediaService localMediaService, RootConfig rootConfig, FilePathProvider filePathProvider) {
        return new LocalStorageStrategy(localMediaService, rootConfig, filePathProvider);
    }
}
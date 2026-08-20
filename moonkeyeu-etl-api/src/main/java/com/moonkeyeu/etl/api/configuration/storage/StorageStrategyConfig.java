package com.moonkeyeu.etl.api.configuration.storage;

import com.moonkeyeu.etl.api.configuration.files.RootConfig;
import com.moonkeyeu.etl.api.configuration.s3.S3Buckets;
import com.moonkeyeu.etl.api.service.LocalMediaService;
import com.moonkeyeu.etl.api.service.S3MediaService;
import com.moonkeyeu.etl.api.strategy.LocalStorageStrategy;
import com.moonkeyeu.etl.api.strategy.S3StorageStrategy;
import com.moonkeyeu.etl.api.strategy.StorageStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageStrategyConfig {

    @Bean("S3_STRATEGY")
    public StorageStrategy s3Strategy(S3MediaService s3MediaService, S3Buckets s3Buckets) {
        return new S3StorageStrategy(s3MediaService, s3Buckets);
    }

    @Bean("LOCAL_STRATEGY")
    public StorageStrategy localStrategy(LocalMediaService localMediaService, RootConfig rootConfig) {
        return new LocalStorageStrategy(localMediaService, rootConfig);
    }
}

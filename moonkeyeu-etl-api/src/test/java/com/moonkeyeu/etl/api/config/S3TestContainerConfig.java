package com.moonkeyeu.etl.api.config;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.Network;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@TestConfiguration(proxyBeanMethods = false)
public class S3TestContainerConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.s3.buckets.bucket-name}")
    private String bucket;

    @Bean
    public Network sharedNetwork() {
        return Network.newNetwork();
    }

    @Bean
    @SuppressWarnings("resource")
    public S3MockContainer s3MockContainer(Network sharedNetwork) {
        return new S3MockContainer("latest")
                .withNetwork(sharedNetwork)
                .withNetworkAliases("aws-mock-s3")
                .withInitialBuckets(bucket);
    }

    @Bean
    public DynamicPropertyRegistrar s3PropertyRegistrar(S3MockContainer container) {
        return registry -> {
            registry.add("aws.s3.endpoint", container::getHttpEndpoint);
            registry.add("aws.region", () -> region);
            registry.add("aws.s3.buckets.bucket-name", () -> bucket);
        };
    }

    @Bean
    public S3Client s3Client(S3MockContainer container) {
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(container.getHttpEndpoint()))
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("test", "test")))
                .build();
    }
}
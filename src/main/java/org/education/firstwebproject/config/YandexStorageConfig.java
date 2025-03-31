package org.education.firstwebproject.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YandexStorageConfig {

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    @Value("${serviceEndpoint}")
    private String serviceEndpoint;

    @Value("${storageRegion}")
    private String storageRegion;

    @Bean
    public AmazonS3 YandexS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                serviceEndpoint, storageRegion
                        )
                )
                .build();
    }
}
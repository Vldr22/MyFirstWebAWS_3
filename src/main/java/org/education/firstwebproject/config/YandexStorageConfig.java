package org.education.firstwebproject.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.education.firstwebproject.utils.YandexStorageProperties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({YandexStorageProperties.class})
public class YandexStorageConfig {

    private final YandexStorageProperties yandexStorageProperties;

    @Bean
    public AmazonS3 yandexS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                        yandexStorageProperties.getAccessKey(),
                        yandexStorageProperties.getSecretKey())))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                yandexStorageProperties.getEndpoint(),
                                yandexStorageProperties.getRegion()
                        )
                )
                .build();
    }
}
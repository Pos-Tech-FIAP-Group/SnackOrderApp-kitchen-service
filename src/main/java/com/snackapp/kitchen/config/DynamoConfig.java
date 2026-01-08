package com.snackapp.kitchen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
public class DynamoConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(
            @Value("${aws.dynamodb.endpoint:}") String endpoint,
            @Value("${aws.dynamodb.region}") String region
    ) {
        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(region));

        // Só adiciona endpoint override se estiver configurado (local)
        if (endpoint != null && !endpoint.isEmpty()) {
            builder.endpointOverride(URI.create(endpoint));
            // Para local, use credenciais dummy
            builder.credentialsProvider(
                    StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("dummy", "dummy")
                    )
            );
        }
        // Para AWS, usa IAM Role automaticamente (não precisa configurar credenciais)

        return builder.build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
    }
}

package com.snackapp.kitchen.bdd;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringContext {

    private static final String QUEUE_NAME = "order.received";
    private static final String TABLE_NAME = "kitchen_orders";
    private static final String GSI_NAME = "gsi_status_createdAt";

    // Containers de infra para TODOS os cenários
    static RabbitMQContainer rabbit =
            new RabbitMQContainer("rabbitmq:3.13-management");

    static GenericContainer<?> dynamo =
            new GenericContainer<>("amazon/dynamodb-local:latest")
                    .withCommand("-jar", "DynamoDBLocal.jar", "-inMemory", "-sharedDb")
                    .withExposedPorts(8000);

    static {
        rabbit.start();
        dynamo.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry r) {
        // Rabbit apontando pro container
        r.add("spring.rabbitmq.host", rabbit::getHost);
        r.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        r.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        r.add("spring.rabbitmq.password", rabbit::getAdminPassword);

        // Dynamo da app (KitchenOrderDynamoAdapter)
        String dynamoEndpoint = "http://" + dynamo.getHost() + ":" + dynamo.getMappedPort(8000);
        r.add("aws.dynamodb.endpoint", () -> dynamoEndpoint);
        r.add("aws.dynamodb.region", () -> "us-east-1");
        r.add("aws.dynamodb.table", () -> TABLE_NAME);
        r.add("aws.dynamodb.statusIndex", () -> GSI_NAME);

        // Pro listener usar a fila correta
        r.add("app.amqp.order-received-queue", () -> QUEUE_NAME);
    }
}
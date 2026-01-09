package com.snackapp.kitchen.bdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackapp.kitchen.adapters.driver.amqp.message.AddOnToKitchenMessage;
import com.snackapp.kitchen.adapters.driver.amqp.message.ItemToKitchenMessage;
import com.snackapp.kitchen.adapters.driver.amqp.message.OrderToKitchenMessage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import org.springframework.core.env.Environment;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KitchenOrderSteps {

    @Autowired
    Environment env;

    private static final String QUEUE_NAME = "order.received";
    private static final String TABLE_NAME = "kitchen_orders";
    private static final String GSI_NAME = "gsi_status_createdAt";

    @Autowired RabbitTemplate rabbitTemplate;
    @Autowired AmqpAdmin amqpAdmin;
    @Autowired TestRestTemplate rest;
    @Autowired ObjectMapper mapper;
    @LocalServerPort int port;

    // Dynamo CLIENT só para ver/criar tabela no cenário
    private DynamoDbClient dynamoClient() {
        // pega o mesmo endpoint que a aplicação está usando
        String endpoint = env.getProperty("aws.dynamodb.endpoint");
        if (endpoint == null) {
            throw new IllegalStateException("Propriedade aws.dynamodb.endpoint não encontrada no contexto de teste");
        }

        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("dummy", "dummy")
                        )
                )
                .build();
    }

    @Given("que a infraestrutura Rabbit e Dynamo está disponível")
    public void infraDisponivel() {
        amqpAdmin.declareQueue(new Queue(QUEUE_NAME, true));
    }

    @Given("que a tabela {string} existe no Dynamo")
    public void tabelaExiste(String table) {
        DynamoDbClient client = dynamoClient();
        try {
            client.describeTable(DescribeTableRequest.builder().tableName(table).build());
            return;
        } catch (ResourceNotFoundException ignored) {}

        client.createTable(CreateTableRequest.builder()
                .tableName(table)
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("orderId").attributeType(ScalarAttributeType.N).build(),
                        AttributeDefinition.builder().attributeName("status").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("createdAt").attributeType(ScalarAttributeType.S).build()
                )
                .keySchema(KeySchemaElement.builder().attributeName("orderId").keyType(KeyType.HASH).build())
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName(GSI_NAME)
                        .keySchema(
                                KeySchemaElement.builder().attributeName("status").keyType(KeyType.HASH).build(),
                                KeySchemaElement.builder().attributeName("createdAt").keyType(KeyType.RANGE).build()
                        )
                        .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                        .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                        .build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                .build());

        client.waiter().waitUntilTableExists(DescribeTableRequest.builder().tableName(table).build());
    }

    @When("publico um pedido na fila com orderId {long} e {int} itens")
    public void publicoPedido(long orderId, int qtdItens) throws Exception {
        var msg = new OrderToKitchenMessage(
                orderId,
                List.of(
                        new ItemToKitchenMessage(
                                "Coca-Cola",
                                1,
                                List.of(new AddOnToKitchenMessage("Queijo Cheddar", 2))
                        ),
                        new ItemToKitchenMessage(
                                "X-Salada Especial",
                                2,
                                List.of()
                        )
                ).subList(0, qtdItens)
        );

        String json = mapper.writeValueAsString(msg);
        rabbitTemplate.convertAndSend("", QUEUE_NAME, json);
    }

    @Then("o pedido {string} deve aparecer na consulta por status {string}")
    public void pedidoApareceNaConsulta(String orderId, String status) throws Exception {
        String url = "http://localhost:" + port + "/kitchen/orders?status=" + status;

        long timeoutMs = 10_000;   // até 10 segundos
        long intervaloMs = 500;    // checa a cada 500ms

        long inicio = System.currentTimeMillis();
        Map<String, Object> encontrado = null;

        while (System.currentTimeMillis() - inicio < timeoutMs && encontrado == null) {
            ResponseEntity<String> resp = rest.getForEntity(url, String.class);

            // garante que não é erro 500, 404 etc
            assertEquals(
                    HttpStatus.OK,
                    resp.getStatusCode(),
                    "HTTP inesperado: " + resp.getStatusCode() + " - body=" + resp.getBody()
            );

            String body = resp.getBody();
            if (body == null || body.isBlank()) {
                Thread.sleep(intervaloMs);
                continue;
            }

            List<Map<String, Object>> lista = mapper.readValue(
                    body,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            encontrado = lista.stream()
                    .filter(p -> String.valueOf(p.get("orderId")).equals(orderId))
                    .findFirst()
                    .orElse(null);

            if (encontrado == null) {
                Thread.sleep(intervaloMs);
            }
        }

        assertNotNull(
                encontrado,
                "Pedido " + orderId + " não encontrado na fila com status " + status
        );
    }

    @When("atualizo o status do pedido {string} para {string}")
    public void atualizoStatus(String orderId, String status) {
        String url = "http://localhost:" + port + "/kitchen/orders/" + orderId + "/status";

        String json = """
        {"status":"%s"}
        """.formatted(status);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(json, headers);

        ResponseEntity<String> resp = rest.exchange(url, HttpMethod.PATCH, entity, String.class);

        assertTrue(
                resp.getStatusCode().is2xxSuccessful(),
                "Falha ao atualizar status. HTTP=" + resp.getStatusCode() + " body=" + resp.getBody()
        );
    }

    @Then("o pedido {string} deve estar com status {string}")
    public void pedidoComStatus(String orderId, String status) throws Exception {
        // Vamos consultar a fila da cozinha filtrando pelo status
        String url = "http://localhost:" + port + "/kitchen/orders?status=" + status;

        int maxTentativas = 10;
        KitchenOrderDto encontrado = null;

        for (int i = 0; i < maxTentativas && encontrado == null; i++) {
            ResponseEntity<String> resp = rest.getForEntity(url, String.class);

            assertTrue(
                    resp.getStatusCode().is2xxSuccessful(),
                    "HTTP inesperado ao buscar fila. HTTP=" + resp.getStatusCode() + " body=" + resp.getBody()
            );

            String body = resp.getBody();
            assertNotNull(body, "Body da resposta é nulo");

            // resposta é um array de KitchenOrder (JSON)
            List<Map<String, Object>> lista = mapper.readValue(
                    body,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            for (Map<String, Object> map : lista) {
                Object idValue = map.get("orderId");
                if (idValue == null) continue;

                String idComoString = String.valueOf(idValue);
                if (idComoString.equals(orderId)) {
                    encontrado = new KitchenOrderDto(
                            idComoString,
                            (String) map.get("status")
                    );
                    break;
                }
            }

            if (encontrado == null) {
                // ainda não chegou no status desejado, espera um pouco e tenta de novo
                Thread.sleep(1_000);
            }
        }

        assertNotNull(encontrado,
                "Pedido %s não encontrado com status %s".formatted(orderId, status));
        assertEquals(status, encontrado.status(),
                "Status do pedido não bate com o esperado");
    }

    // DTO simples só para ajudar no teste BDD
    record KitchenOrderDto(String orderId, String status) {}
}
Feature: Fluxo da cozinha ao receber pedido

  Scenario: Receber pedido via fila e atualizar status
    Given que a infraestrutura Rabbit e Dynamo está disponível
    And que a tabela "kitchen_orders" existe no Dynamo
    When publico um pedido na fila com orderId 1 e 2 itens
    Then o pedido "1" deve aparecer na consulta por status "RECEBIDO"
    When atualizo o status do pedido "1" para "EM_PREPARACAO"
    Then o pedido "1" deve estar com status "EM_PREPARACAO"
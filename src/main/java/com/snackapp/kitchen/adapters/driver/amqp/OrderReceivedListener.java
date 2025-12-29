package com.snackapp.kitchen.adapters.driver.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackapp.kitchen.adapters.driver.api.dto.request.OrderReceivedMessage;
import com.snackapp.kitchen.core.application.usecases.ReceiveOrderUseCase;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReceivedListener {

    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final ReceiveOrderUseCase receiveOrderUseCase;

    @RabbitListener(queues = "${app.amqp.order-received-queue}")
    public void onMessage(Message message) {
        try {
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);

            OrderReceivedMessage dto = objectMapper.readValue(payload, OrderReceivedMessage.class);

            var violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            receiveOrderUseCase.receive(dto);

        } catch (Exception e) {
            log.error("Mensagem inválida. Rejeitando sem requeue.", e);
            throw new AmqpRejectAndDontRequeueException("Mensagem inválida", e);
        }
    }
}
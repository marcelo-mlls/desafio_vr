package com.desafio.pedidos_service.service;

import com.desafio.pedidos_service.configurartion.RabbitMQConfig;
import com.desafio.pedidos_service.exception.ProcessamentoException;
import com.desafio.pedidos_service.model.Pedido;
import com.desafio.pedidos_service.model.StatusPedido;
import com.desafio.pedidos_service.service.PedidoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PedidoConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final PedidoService pedidoService;
    private final Random random = new Random();

    @Autowired
    public PedidoConsumer(RabbitTemplate rabbitTemplate, PedidoService pedidoService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pedidoService = pedidoService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ENTRADA)
    public void processarPedido(Pedido pedido) {
        log.info("Processando pedido: {}", pedido.getId());
        pedidoService.atualizarStatus(pedido.getId(), "PROCESSANDO");

        try {
            TimeUnit.SECONDS.sleep(random.nextInt(1, 4));

            if (random.nextDouble() < 0.2) {
                throw new ProcessamentoException("Falha simulada no processamento do pedido: " + pedido.getId());
            }

            handleSuccess(pedido);

        } catch (Exception e) {
            handleFailure(pedido, e);
            throw new ProcessamentoException("Falha ao processar o pedido: " + e.getMessage());
        }
    }

    private void handleSuccess(Pedido pedido) {
        log.info("Pedido {} processado com sucesso!", pedido.getId());
        pedidoService.atualizarStatus(pedido.getId(), "SUCESSO");

        StatusPedido status = StatusPedido.newSuccess(pedido.getId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.QUEUE_STATUS_SUCESSO, status);
    }

    private void handleFailure(Pedido pedido, Exception e) {
        log.error("Falha no processamento do pedido {}: {}", pedido.getId(), e.getMessage());
        pedidoService.atualizarStatus(pedido.getId(), "FALHA");

        StatusPedido status = StatusPedido.newFailure(pedido.getId(), e.getMessage());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.QUEUE_STATUS_FALHA, status);
    }
}
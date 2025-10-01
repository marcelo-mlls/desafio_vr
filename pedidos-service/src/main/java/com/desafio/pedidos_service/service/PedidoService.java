package com.desafio.pedidos_service.service;

import com.desafio.pedidos_service.configurartion.RabbitMQConfig;
import com.desafio.pedidos_service.model.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class PedidoService {

    private final RabbitTemplate rabbitTemplate;
    private final Map<UUID, String> statusMap = new ConcurrentHashMap<>();

    @Autowired
    public PedidoService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Pedido criarPedido(Pedido pedido) {
        pedido.setId(UUID.randomUUID());
        pedido.setDataCriacao(LocalDateTime.now());

        log.info("Enviando pedido para a fila: {}", pedido.getId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.QUEUE_ENTRADA, pedido);

        statusMap.put(pedido.getId(), "RECEBIDO, AGUARDANDO PROCESSAMENTO");
        log.info("Pedido {} recebido e status inicial definido.", pedido.getId());

        return pedido;
    }

    public Optional<String> consultarStatus(UUID id) {
        return Optional.ofNullable(statusMap.get(id));
    }

    public void atualizarStatus(UUID id, String status) {
        statusMap.put(id, status);
        log.info("Status do pedido {} atualizado para: {}", id, status);
    }
}
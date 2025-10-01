package com.desafio.pedidos_service.service;

import com.desafio.pedidos_service.configurartion.RabbitMQConfig;
import com.desafio.pedidos_service.model.Pedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void quandoCriarPedido_DeveEnviarMensagemParaFila() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setProduto("Test Product");
        pedido.setQuantidade(10);

        // Act
        Pedido resultado = pedidoService.criarPedido(pedido);

        // Assert
        assertNotNull(resultado.getId());
        assertNotNull(resultado.getDataCriacao());

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(rabbitTemplate).convertAndSend(
                org.mockito.ArgumentMatchers.eq(RabbitMQConfig.EXCHANGE_NAME),
                org.mockito.ArgumentMatchers.eq(RabbitMQConfig.QUEUE_ENTRADA),
                pedidoCaptor.capture()
        );

        Pedido pedidoEnviado = pedidoCaptor.getValue();
        assertEquals("Test Product", pedidoEnviado.getProduto());
        assertEquals(10, pedidoEnviado.getQuantidade());
    }
}
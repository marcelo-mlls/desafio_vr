package com.desafio.pedidos_service.configurartion;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "pedidos.exchange";
    public static final String QUEUE_ENTRADA = "pedidos.entrada.jules";
    public static final String QUEUE_ENTRADA_DLQ = "pedidos.entrada.jules.dlq";
    public static final String QUEUE_STATUS_SUCESSO = "pedidos.status.sucesso.jules";
    public static final String QUEUE_STATUS_FALHA = "pedidos.status.falha.jules";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue dlq() {
        return new Queue(QUEUE_ENTRADA_DLQ);
    }

    @Bean
    public Queue entradaQueue() {
        return QueueBuilder.durable(QUEUE_ENTRADA)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_ENTRADA_DLQ)
                .build();
    }

    @Bean
    public Queue statusSucessoQueue() {
        return new Queue(QUEUE_STATUS_SUCESSO);
    }

    @Bean
    public Queue statusFalhaQueue() {
        return new Queue(QUEUE_STATUS_FALHA);
    }

    @Bean
    public Binding entradaBinding(Queue entradaQueue, DirectExchange exchange) {
        return BindingBuilder.bind(entradaQueue).to(exchange).with(QUEUE_ENTRADA);
    }

    @Bean
    public Binding dlqBinding(Queue dlq, DirectExchange exchange) {
        return BindingBuilder.bind(dlq).to(exchange).with(QUEUE_ENTRADA_DLQ);
    }

    @Bean
    public Binding statusSucessoBinding(Queue statusSucessoQueue, DirectExchange exchange) {
        return BindingBuilder.bind(statusSucessoQueue).to(exchange).with(QUEUE_STATUS_SUCESSO);
    }

    @Bean
    public Binding statusFalhaBinding(Queue statusFalhaQueue, DirectExchange exchange) {
        return BindingBuilder.bind(statusFalhaQueue).to(exchange).with(QUEUE_STATUS_FALHA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
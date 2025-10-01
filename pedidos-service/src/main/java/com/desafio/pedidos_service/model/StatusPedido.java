package com.desafio.pedidos_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusPedido {

    private UUID idPedido;
    private Status status;
    private LocalDateTime dataProcessamento;
    private String mensagemErro;

    public enum Status {
        SUCESSO, FALHA
    }

    public static StatusPedido newSuccess(UUID idPedido) {
        return new StatusPedido(idPedido, Status.SUCESSO, LocalDateTime.now(), null);
    }

    public static StatusPedido newFailure(UUID idPedido, String message) {
        return new StatusPedido(idPedido, Status.FALHA, LocalDateTime.now(), message);
    }
}
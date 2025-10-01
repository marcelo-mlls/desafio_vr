package com.desafio.pedidos_service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    private UUID id;

    @NotEmpty(message = "O produto não pode estar vazio")
    private String produto;

    @Min(value = 1, message = "A quantidade deve ser maior que zero")
    private int quantidade;

    private LocalDateTime dataCriacao;
}
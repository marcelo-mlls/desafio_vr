package org.desafio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pedido {
    private UUID id;
    private String produto;
    private int quantidade;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime dataCriacao;

    // Getters and Setters for all fields

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
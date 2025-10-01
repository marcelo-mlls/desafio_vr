package com.desafio.pedidos_service.controller;

import com.desafio.pedidos_service.model.Pedido;
import com.desafio.pedidos_service.service.PedidoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;

    @Autowired
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@Valid @RequestBody Pedido pedido) {
        log.info("Recebida requisição para criar pedido: {}", pedido);
        Pedido novoPedido = pedidoService.criarPedido(pedido);
        return ResponseEntity.accepted().body(novoPedido);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> consultarStatus(@PathVariable UUID id) {
        log.info("Recebida requisição para consultar status do pedido: {}", id);
        return pedidoService.consultarStatus(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package org.desafio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.desafio.model.Pedido;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PedidoApiClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String BASE_URL = "http://localhost:8080/api/pedidos";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public Pedido enviarPedido(String produto, int quantidade) throws IOException {
        Map<String, Object> pedidoMap = Map.of("produto", produto, "quantidade", quantidade);
        String jsonBody = mapper.writeValueAsString(pedidoMap);

        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Falha ao enviar pedido: " + response.code() + " - " + (response.body() != null ? response.body().string() : "Sem corpo de resposta"));
            }
            if (response.body() == null) {
                throw new IOException("Resposta vazia do servidor");
            }
            return mapper.readValue(response.body().string(), Pedido.class);
        }
    }

    public String consultarStatus(UUID id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/status/" + id)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "ERRO " + response.code();
            }
            if (response.body() == null) {
                return "ERRO: Resposta vazia";
            }
            return response.body().string();
        }
    }
}
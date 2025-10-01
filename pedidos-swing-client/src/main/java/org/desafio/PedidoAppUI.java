package org.desafio;

import org.desafio.model.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class PedidoAppUI extends JFrame {

    private final JTextField produtoField = new JTextField(20);
    private final JTextField quantidadeField = new JTextField(5);
    private final JButton enviarButton = new JButton("Enviar Pedido");
    private final DefaultTableModel tableModel;

    private final PedidoApiClient apiClient = new PedidoApiClient();

    private final List<UUID> pedidosAguardando = new CopyOnWriteArrayList<>();

    public PedidoAppUI() {
        setTitle("Envio de Pedidos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Produto:"));
        inputPanel.add(produtoField);
        inputPanel.add(new JLabel("Quantidade:"));
        inputPanel.add(quantidadeField);
        inputPanel.add(enviarButton);
        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID do Pedido", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable pedidosTable = new JTable(tableModel);
        add(new JScrollPane(pedidosTable), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        enviarButton.addActionListener(e -> enviarNovoPedido());
        iniciarPollingDeStatus();
    }

    private void enviarNovoPedido() {
        // Validação básica da entrada do usuário
        String produto = produtoField.getText();
        if (produto == null || produto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do produto não pode ser vazio.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeField.getText());
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "A quantidade deve ser um número inteiro maior que zero.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        enviarButton.setEnabled(false);

        new SwingWorker<Pedido, Void>() {
            @Override
            protected Pedido doInBackground() throws Exception {
                return apiClient.enviarPedido(produto, quantidade);
            }

            @Override
            protected void done() {
                try {
                    Pedido novoPedido = get();
                    UUID pedidoId = novoPedido.getId();

                    tableModel.addRow(new Object[]{pedidoId.toString(), "ENVIADO, AGUARDANDO PROCESSO"});
                    pedidosAguardando.add(pedidoId);

                    produtoField.setText("");
                    quantidadeField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(PedidoAppUI.this, "Erro ao enviar pedido:\n" + ex.getMessage(), "Erro de Comunicação", JOptionPane.ERROR_MESSAGE);
                } finally {
                    enviarButton.setEnabled(true);
                }
            }
        }.execute();
    }


    private void iniciarPollingDeStatus() {
        Timer timer = new Timer(5000, e -> atualizarStatusDosPedidos());
        timer.start();
    }


    private void atualizarStatusDosPedidos() {
        if (pedidosAguardando.isEmpty()) {
            return;
        }

        for (UUID pedidoId : pedidosAguardando) {
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    // Consulta o status em segundo plano
                    return apiClient.consultarStatus(pedidoId);
                }

                @Override
                protected void done() {
                    try {
                        String novoStatus = get();
                        if (novoStatus != null && !novoStatus.contains("AGUARDANDO")) {
                            atualizarLinhaTabela(pedidoId, novoStatus);
                            pedidosAguardando.remove(pedidoId); // Remove da lista para não verificar mais.
                        }
                    } catch (Exception ex) {
                        System.err.println("Falha ao consultar status para o pedido " + pedidoId + ": " + ex.getMessage());
                    }
                }
            }.execute();
        }
    }

    private void atualizarLinhaTabela(UUID pedidoId, String status) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(pedidoId.toString())) {
                    tableModel.setValueAt(status, i, 1);
                    break;
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PedidoAppUI().setVisible(true));
    }
}
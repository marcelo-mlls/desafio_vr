package org.desafio;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PedidoAppUI ui = new PedidoAppUI();
            ui.setVisible(true);
        });
    }
}
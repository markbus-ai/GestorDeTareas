package com.trullo.frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class PanelRecordatorios extends JPanel {

    private final CalendarioMensual calendario;

    private final JLabel labelFecha;

    private final JPanel listaContenedor;

    private final JTextField campoTexto;

    private LocalDate fechaActualSeleccionada;

    public PanelRecordatorios(CalendarioMensual calendario) {
        this.calendario = calendario;
        this.fechaActualSeleccionada = LocalDate.now();

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 48, 0, 0));
        setPreferredSize(new Dimension(340, 500));

        labelFecha = new JLabel();
        labelFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelFecha.setForeground(ThemeManager.texto());
        labelFecha.setBorder(BorderFactory.createEmptyBorder(0, 4, 16, 0));

        listaContenedor = new JPanel();
        listaContenedor.setLayout(new BoxLayout(listaContenedor, BoxLayout.Y_AXIS));
        listaContenedor.setOpaque(false);

        JScrollPane scrollLista = new JScrollPane(listaContenedor);
        scrollLista.setOpaque(false);
        scrollLista.getViewport().setOpaque(false);
        scrollLista.setBorder(null);
        scrollLista.getVerticalScrollBar().setUnitIncrement(12);
        scrollLista.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollLista.setPreferredSize(new Dimension(340, 300));

        campoTexto = new JTextField();
        campoTexto.putClientProperty("JTextField.placeholderText", "Escribí un recordatorio...");
        campoTexto.putClientProperty("JComponent.arc", 12);
        campoTexto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoTexto.setBackground(ThemeManager.input());
        campoTexto.setForeground(ThemeManager.texto());
        campoTexto.setCaretColor(ThemeManager.texto());
        campoTexto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                new EmptyBorder(8, 12, 8, 12)));
        campoTexto.setPreferredSize(new Dimension(0, 38));

        JButton botonAgregar = new JButton("+ Agregar");
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botonAgregar.setFocusPainted(false);
        botonAgregar.setPreferredSize(new Dimension(0, 40));
        botonAgregar.putClientProperty("JButton.buttonType", "roundRect");
        botonAgregar.putClientProperty("JButton.arc", 12);
        botonAgregar.setBackground(ThemeManager.NARANJA);
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel panelInferior = new JPanel(new BorderLayout(0, 10));
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        panelInferior.add(campoTexto, BorderLayout.NORTH);
        panelInferior.add(botonAgregar, BorderLayout.SOUTH);

        add(labelFecha, BorderLayout.NORTH);
        add(scrollLista, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        calendario.setOnFechaSeleccionada(fecha -> {
            fechaActualSeleccionada = fecha;
            actualizarPanel();
        });

        botonAgregar.addActionListener(e -> {
            String texto = campoTexto.getText().trim();
            if (!texto.isEmpty()) {
                calendario.agregarRecordatorio(fechaActualSeleccionada, texto);
                campoTexto.setText("");
                actualizarPanel();
            }
        });

        ThemeManager.onThemeChange(() -> {
            labelFecha.setForeground(ThemeManager.texto());
            campoTexto.setBackground(ThemeManager.input());
            campoTexto.setForeground(ThemeManager.texto());
            campoTexto.setCaretColor(ThemeManager.texto());
            campoTexto.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                    new EmptyBorder(8, 12, 8, 12)));
            botonAgregar.setBackground(ThemeManager.NARANJA);
            actualizarPanel();
            revalidate();
            repaint();
        });

        actualizarPanel();
    }

    private void actualizarPanel() {

        labelFecha.setText("Recordatorios \u00B7 " + fechaActualSeleccionada.getDayOfMonth() +
                "/" + fechaActualSeleccionada.getMonthValue() + "/" + fechaActualSeleccionada.getYear());

        listaContenedor.removeAll();

        List<String> textos = calendario.obtenerRecordatorios(fechaActualSeleccionada);

        if (textos.isEmpty()) {
            JLabel vacio = new JLabel("No hay recordatorios");
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vacio.setForeground(ThemeManager.textoSuave());
            vacio.setBorder(new EmptyBorder(12, 4, 0, 0));
            listaContenedor.add(vacio);
        } else {
            for (String texto : textos) {
                JPanel tarjeta = crearTarjetaRecordatorio(texto, () -> {
                    calendario.borrarRecordatorio(fechaActualSeleccionada, texto);
                    actualizarPanel();
                });
                listaContenedor.add(tarjeta);
                listaContenedor.add(Box.createVerticalStrut(6));
            }
        }

        listaContenedor.revalidate();
        listaContenedor.repaint();
    }

    private JPanel crearTarjetaRecordatorio(String texto, Runnable alBorrar) {
        JPanel tarjeta = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(11, 14, 11, 14));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel punto = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.fillOval(3, 3, 6, 6);
                g2.dispose();
            }
        };
        punto.setPreferredSize(new Dimension(14, 14));
        punto.setForeground(ThemeManager.NARANJA);
        punto.setBorder(new EmptyBorder(0, 0, 0, 6));

        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.texto());

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setOpaque(false);
        izquierda.add(punto, BorderLayout.WEST);
        izquierda.add(label, BorderLayout.CENTER);

        JLabel botonBorrar = new JLabel("x") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("x")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("x", x, y);
                g2.dispose();
            }
        };
        botonBorrar.setForeground(ThemeManager.textoSuave());
        botonBorrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonBorrar.setPreferredSize(new Dimension(24, 24));
        botonBorrar.setHorizontalAlignment(SwingConstants.CENTER);

        botonBorrar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonBorrar.setForeground(ThemeManager.ROSA); }
            public void mouseExited(MouseEvent e) { botonBorrar.setForeground(ThemeManager.textoSuave()); }
        });

        botonBorrar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { alBorrar.run(); }
        });

        tarjeta.add(izquierda, BorderLayout.CENTER);
        tarjeta.add(botonBorrar, BorderLayout.EAST);

        ThemeManager.onThemeChange(() -> {
            label.setForeground(ThemeManager.texto());
            punto.setForeground(ThemeManager.NARANJA);
            botonBorrar.setForeground(ThemeManager.textoSuave());
            tarjeta.repaint();
        });

        return tarjeta;
    }
}

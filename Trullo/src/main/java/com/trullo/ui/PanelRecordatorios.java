package com.trullo.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

// Este panel muestra los recordatorios de una fecha seleccionada en el calendario
// tiene un label con la fecha, una lista de recordatorios, y un campo para agregar nuevos
// se conecta con el CalendarioMensual para saber que fecha esta seleccionada
public class PanelRecordatorios extends JPanel {

    // referencia al calendario para saber la fecha y guardar recordatorios
    private final CalendarioMensual calendario;

    // label que muestra la fecha seleccionada
    private final JLabel labelFecha;

    // panel donde van las tarjetas de recordatorios
    private final JPanel listaContenedor;

    // campo de texto para escribir un nuevo recordatorio
    private final JTextField campoTexto;

    // la fecha que esta seleccionada actualmente en el calendario
    private LocalDate fechaActualSeleccionada;

    // constructor, recibe el calendario para conectarse con el
    public PanelRecordatorios(CalendarioMensual calendario) {
        this.calendario = calendario;
        this.fechaActualSeleccionada = LocalDate.now(); // arranca con la fecha de hoy

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 48, 0, 0));
        setPreferredSize(new Dimension(340, 500));

        // label de la fecha
        labelFecha = new JLabel();
        labelFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelFecha.setForeground(ThemeManager.texto());
        labelFecha.setBorder(BorderFactory.createEmptyBorder(0, 4, 16, 0));

        // panel donde van las tarjetas de recordatorios
        listaContenedor = new JPanel();
        listaContenedor.setLayout(new BoxLayout(listaContenedor, BoxLayout.Y_AXIS));
        listaContenedor.setOpaque(false);

        // scroll para la lista de recordatorios
        JScrollPane scrollLista = new JScrollPane(listaContenedor);
        scrollLista.setOpaque(false);
        scrollLista.getViewport().setOpaque(false);
        scrollLista.setBorder(null);
        scrollLista.getVerticalScrollBar().setUnitIncrement(12);
        scrollLista.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollLista.setPreferredSize(new Dimension(340, 300));

        // campo de texto para escribir recordatorios
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

        // boton para agregar el recordatorio
        JButton botonAgregar = new JButton("+ Agregar");
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botonAgregar.setFocusPainted(false);
        botonAgregar.setPreferredSize(new Dimension(0, 40));
        botonAgregar.putClientProperty("JButton.buttonType", "roundRect");
        botonAgregar.putClientProperty("JButton.arc", 12);
        botonAgregar.setBackground(ThemeManager.NARANJA); // naranja porque es un recordatorio
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // panel inferior con el campo y el boton
        JPanel panelInferior = new JPanel(new BorderLayout(0, 10));
        panelInferior.setOpaque(false);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        panelInferior.add(campoTexto, BorderLayout.NORTH);
        panelInferior.add(botonAgregar, BorderLayout.SOUTH);

        // acomodamos todo en el panel
        add(labelFecha, BorderLayout.NORTH);
        add(scrollLista, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // cuando seleccionan una fecha en el calendario, actualizamos el panel
        calendario.setOnFechaSeleccionada(fecha -> {
            fechaActualSeleccionada = fecha;
            actualizarPanel();
        });

        // cuando clickean "Agregar", guardamos el recordatorio
        botonAgregar.addActionListener(e -> {
            String texto = campoTexto.getText().trim();
            if (!texto.isEmpty()) {
                calendario.agregarRecordatorio(fechaActualSeleccionada, texto);
                campoTexto.setText(""); // limpiamos el campo
                actualizarPanel();
            }
        });

        // listener para cambio de tema
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

        actualizarPanel(); // dibujamos por primera vez
    }

    // actualiza todo el panel: fecha, lista de recordatorios, etc
    private void actualizarPanel() {
        // formateamos la fecha como dd/mm/yyyy
        labelFecha.setText("Recordatorios \u00B7 " + fechaActualSeleccionada.getDayOfMonth() +
                "/" + fechaActualSeleccionada.getMonthValue() + "/" + fechaActualSeleccionada.getYear());

        listaContenedor.removeAll(); // limpiamos la lista

        // obtenemos los recordatorios de la fecha seleccionada
        List<String> textos = calendario.obtenerRecordatorios(fechaActualSeleccionada);

        if (textos.isEmpty()) {
            // si no hay recordatorios, mostramos mensaje
            JLabel vacio = new JLabel("No hay recordatorios");
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vacio.setForeground(ThemeManager.textoSuave());
            vacio.setBorder(new EmptyBorder(12, 4, 0, 0));
            listaContenedor.add(vacio);
        } else {
            // por cada recordatorio, creamos una tarjeta
            for (String texto : textos) {
                JPanel tarjeta = crearTarjetaRecordatorio(texto, () -> {
                    // callback para borrar: le dice al calendario que lo borre
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

    // crea una tarjeta visual para un recordatorio
    // tiene el texto a la izquierda y un boton X para borrar a la derecha
    private JPanel crearTarjetaRecordatorio(String texto, Runnable alBorrar) {
        // la tarjeta con fondo redondeado
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

        // puntito naranja antes del texto, pintado manualmente
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

        // texto del recordatorio
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(ThemeManager.texto());

        // panel izquierdo con el puntito y el texto
        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setOpaque(false);
        izquierda.add(punto, BorderLayout.WEST);
        izquierda.add(label, BorderLayout.CENTER);

        // botoncito de borrar pintado manualmente
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

        // efecto hover: se pone rosa cuando pasas el mouse
        botonBorrar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonBorrar.setForeground(ThemeManager.ROSA); }
            public void mouseExited(MouseEvent e) { botonBorrar.setForeground(ThemeManager.textoSuave()); }
        });

        // cuando clickean la X, borra el recordatorio
        botonBorrar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { alBorrar.run(); }
        });

        tarjeta.add(izquierda, BorderLayout.CENTER);
        tarjeta.add(botonBorrar, BorderLayout.EAST);

        // listener para cambio de tema
        ThemeManager.onThemeChange(() -> {
            label.setForeground(ThemeManager.texto());
            punto.setForeground(ThemeManager.NARANJA);
            botonBorrar.setForeground(ThemeManager.textoSuave());
            tarjeta.repaint();
        });

        return tarjeta;
    }
}

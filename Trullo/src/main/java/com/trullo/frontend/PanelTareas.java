package com.trullo.frontend;

import com.trullo.recursos.EmojiIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelTareas extends JPanel {

    private final JPanel listaTareas;

    private final List<TareaData> tareas = new ArrayList<>();

    private final Map<TareaData, JPanel> tareasPanelMap = new HashMap<>();

    private String filtroActual = "Todas";

    private final JButton btnTodas, btnPendientes, btnCompletadas;
    private final JLabel titulo;

    public PanelTareas() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));
        setBackground(ThemeManager.fondo());

        titulo = new JLabel("My Tasks");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFiltros.setOpaque(false);

        btnTodas = crearBotonFiltro("Todas", true);
        btnPendientes = crearBotonFiltro("Pendientes", false);
        btnCompletadas = crearBotonFiltro("Completadas", false);

        btnTodas.addActionListener(e -> { filtroActual = "Todas"; actualizarFiltros(btnTodas, btnPendientes, btnCompletadas); refrescarLista(); });
        btnPendientes.addActionListener(e -> { filtroActual = "Pendientes"; actualizarFiltros(btnPendientes, btnTodas, btnCompletadas); refrescarLista(); });
        btnCompletadas.addActionListener(e -> { filtroActual = "Completadas"; actualizarFiltros(btnCompletadas, btnTodas, btnPendientes); refrescarLista(); });

        panelFiltros.add(btnTodas);
        panelFiltros.add(btnPendientes);
        panelFiltros.add(btnCompletadas);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setOpaque(false);
        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(18));
        panelSuperior.add(panelFiltros);

        listaTareas = new JPanel();
        listaTareas.setLayout(new BoxLayout(listaTareas, BoxLayout.Y_AXIS));
        listaTareas.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listaTareas);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        JButton botonAgregar = new JButton("+") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("+")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString("+", x, y);
                g2.dispose();
            }
        };
        botonAgregar.setFont(new Font("Segoe UI", Font.BOLD, 28));
        botonAgregar.setForeground(Color.WHITE);
        botonAgregar.setBackground(ThemeManager.AZUL);
        botonAgregar.setContentAreaFilled(false);
        botonAgregar.setOpaque(false);
        botonAgregar.setBorderPainted(false);
        botonAgregar.setFocusPainted(false);
        botonAgregar.setPreferredSize(new Dimension(52, 52));
        botonAgregar.setMinimumSize(new Dimension(52, 52));
        botonAgregar.setMaximumSize(new Dimension(52, 52));
        botonAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setOpaque(false);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelBoton.add(botonAgregar);

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);

        botonAgregar.addActionListener(e -> mostrarDialogoNuevaTarea());

        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            botonAgregar.setBackground(ThemeManager.AZUL);
            actualizarFiltrosActivo();
            refrescarLista();
            revalidate();
            repaint();
        });
    }

    private JButton crearBotonFiltro(String texto, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(activo ? Color.WHITE : ThemeManager.textoSuave());
        btn.setBackground(activo ? ThemeManager.AZUL : ThemeManager.filtroInactivo());
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        btn.putClientProperty("JButton.buttonType", "roundRect");
        btn.putClientProperty("JButton.arc", 20);
        return btn;
    }

    private void actualizarFiltros(JButton activo, JButton... inactivos) {
        activo.setBackground(ThemeManager.AZUL);
        activo.setForeground(Color.WHITE);
        for (JButton btn : inactivos) {
            btn.setBackground(ThemeManager.filtroInactivo());
            btn.setForeground(ThemeManager.textoSuave());
        }
    }

    private void actualizarFiltrosActivo() {
        for (JButton btn : new JButton[]{btnTodas, btnPendientes, btnCompletadas}) {
            boolean activo = btn.getText().equals(filtroActual);
            btn.setBackground(activo ? ThemeManager.AZUL : ThemeManager.filtroInactivo());
            btn.setForeground(activo ? Color.WHITE : ThemeManager.textoSuave());
        }
    }

    private void refrescarLista() {
        listaTareas.removeAll();
        boolean hayAlgo = false;
        for (TareaData tarea : tareas) {

            boolean mostrar = switch (filtroActual) {
                case "Pendientes" -> !tarea.completada;
                case "Completadas" -> tarea.completada;
                default -> true;
            };
            if (mostrar) {
                JPanel tarjeta = tareasPanelMap.get(tarea);
                if (tarjeta != null) {
                    listaTareas.add(tarjeta);
                    listaTareas.add(Box.createVerticalStrut(6));
                    hayAlgo = true;
                }
            }
        }

        if (!hayAlgo) {
            JLabel vacio = new JLabel("No hay tareas", SwingConstants.CENTER);
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vacio.setForeground(ThemeManager.textoSuave());
            vacio.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
            listaTareas.add(vacio);
        }
        listaTareas.revalidate();
        listaTareas.repaint();
    }

    private void mostrarDialogoNuevaTarea() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva tarea", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.dialogoFondo());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        panelPrincipal.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.tarjeta());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 56));
        header.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 16));

        JLabel labelHeader = new JLabel("  Nueva tarea");
        labelHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        labelHeader.setForeground(ThemeManager.texto());

        JButton botonCerrar = new JButton("x");
        botonCerrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botonCerrar.setForeground(ThemeManager.textoSuave());
        botonCerrar.setBackground(null);
        botonCerrar.setContentAreaFilled(false);
        botonCerrar.setBorderPainted(false);
        botonCerrar.setFocusPainted(false);
        botonCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonCerrar.setPreferredSize(new Dimension(32, 32));
        botonCerrar.addActionListener(e -> dialog.dispose());

        header.add(labelHeader, BorderLayout.WEST);
        header.add(botonCerrar, BorderLayout.EAST);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 28, 16, 28));

        JLabel labelTarea = new JLabel("Tarea");
        labelTarea.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTarea.setForeground(ThemeManager.texto());
        labelTarea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField campoTarea = new JTextField();
        campoTarea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoTarea.putClientProperty("JTextField.placeholderText", "Escribí la tarea...");
        campoTarea.putClientProperty("JComponent.arc", 12);
        campoTarea.setBackground(ThemeManager.input());
        campoTarea.setForeground(ThemeManager.texto());
        campoTarea.setCaretColor(ThemeManager.texto());
        campoTarea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                new EmptyBorder(10, 14, 10, 14)));
        campoTarea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campoTarea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelTiempo = new JLabel("Tiempo (opcional)");
        labelTiempo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTiempo.setForeground(ThemeManager.texto());
        labelTiempo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField campoTiempo = new JTextField();
        campoTiempo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoTiempo.putClientProperty("JTextField.placeholderText", "ej: 2 dias, 3 horas...");
        campoTiempo.putClientProperty("JComponent.arc", 12);
        campoTiempo.setBackground(ThemeManager.input());
        campoTiempo.setForeground(ThemeManager.texto());
        campoTiempo.setCaretColor(ThemeManager.texto());
        campoTiempo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                new EmptyBorder(10, 14, 10, 14)));
        campoTiempo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campoTiempo.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenido.add(labelTarea);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(campoTarea);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(labelTiempo);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(campoTiempo);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(8, 0, 16, 0));

        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        botonCancelar.setForeground(ThemeManager.textoSuave());
        botonCancelar.setBackground(ThemeManager.input());
        botonCancelar.setBorderPainted(false);
        botonCancelar.setFocusPainted(false);
        botonCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonCancelar.setPreferredSize(new Dimension(100, 36));
        botonCancelar.addActionListener(e -> dialog.dispose());

        JButton botonCrear = new JButton("Crear tarea");
        botonCrear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botonCrear.setForeground(Color.WHITE);
        botonCrear.setBackground(ThemeManager.AZUL);
        botonCrear.setBorderPainted(false);
        botonCrear.setFocusPainted(false);
        botonCrear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonCrear.setPreferredSize(new Dimension(110, 36));
        botonCrear.addActionListener(e -> {
            String texto = campoTarea.getText().trim();
            String tiempo = campoTiempo.getText().trim();
            if (!texto.isEmpty()) {
                agregarTarea(texto, tiempo.isEmpty() ? null : tiempo);
                dialog.dispose();
            } else {
                campoTarea.requestFocus();
                campoTarea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.ROSA, 2),
                        new EmptyBorder(10, 14, 10, 14)));
            }
        });

        panelPrincipal.registerKeyboardAction(e -> botonCrear.doClick(), KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        footer.add(botonCancelar);
        footer.add(botonCrear);

        panelPrincipal.add(header, BorderLayout.NORTH);
        panelPrincipal.add(contenido, BorderLayout.CENTER);
        panelPrincipal.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(panelPrincipal);
        dialog.getRootPane().setDefaultButton(botonCrear);
        dialog.setVisible(true);
    }

    public void agregarTarea(String texto, String deadline) {
        TareaData tarea = new TareaData(texto, deadline);
        tareas.add(tarea);
        JPanel tarjeta = crearTarjetaTarea(tarea);
        tareasPanelMap.put(tarea, tarjeta);
        refrescarLista();
    }

    private JPanel crearTarjetaTarea(TareaData tarea) {

        JPanel tarjeta = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(12, 16, 12, 16));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox check = new JCheckBox() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight());
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                int margen = 2;
                int radio = size - margen * 2;

                if (isSelected()) {

                    g2.setColor(ThemeManager.AZUL);
                    g2.fillRoundRect(x + margen, y + margen, radio, radio, 8, 8);

                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = x + margen;
                    int cy = y + margen;
                    g2.drawLine(cx + 4, cy + radio / 2, cx + radio / 3, cy + radio - 5);
                    g2.drawLine(cx + radio / 3, cy + radio - 5, cx + radio - 5, cy + 4);
                } else {

                    g2.setColor(ThemeManager.borde());
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(x + margen, y + margen, radio - 1, radio - 1, 8, 8);
                }
                g2.dispose();
            }
        };
        check.setOpaque(false);
        check.setFocusPainted(false);
        check.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        check.setPreferredSize(new Dimension(22, 22));

        JLabel labelTexto = new JLabel(tarea.texto);
        labelTexto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelTexto.setForeground(ThemeManager.texto());

        JPanel izquierda = new JPanel(new BorderLayout(10, 0));
        izquierda.setOpaque(false);
        izquierda.add(check, BorderLayout.WEST);
        izquierda.add(labelTexto, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derecha.setOpaque(false);

        JLabel[] labelDeadlineHolder = new JLabel[1];
        if (tarea.deadline != null && !tarea.deadline.isEmpty()) {
            JLabel labelDeadline = new JLabel(" " + tarea.deadline + " ");
            labelDeadline.setFont(new Font("Segoe UI", Font.BOLD, 11));
            labelDeadline.setForeground(ThemeManager.CELESTE);
            labelDeadline.setOpaque(true);
            labelDeadline.setBackground(ThemeManager.deadlineBg());
            labelDeadline.setBorder(new EmptyBorder(4, 10, 4, 10));
            labelDeadlineHolder[0] = labelDeadline;
            derecha.add(labelDeadline);
        }

        tarjeta.add(izquierda, BorderLayout.CENTER);
        tarjeta.add(derecha, BorderLayout.EAST);

        check.addActionListener(e -> {
            if (check.isSelected()) {
                tarea.completada = true;

                Map<TextAttribute, Object> attrs = new HashMap<>(labelTexto.getFont().getAttributes());
                attrs.put(TextAttribute.STRIKETHROUGH, Boolean.TRUE);
                labelTexto.setFont(labelTexto.getFont().deriveFont(attrs));
                labelTexto.setForeground(ThemeManager.textoSuave());

                Timer timer = new Timer(1000, e2 -> {
                    tareas.remove(tarea);
                    tareasPanelMap.remove(tarea);
                    refrescarLista();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        ThemeManager.onThemeChange(() -> {
            labelTexto.setForeground(ThemeManager.texto());
            if (labelDeadlineHolder[0] != null) {
                labelDeadlineHolder[0].setBackground(ThemeManager.deadlineBg());
            }
            tarjeta.repaint();
        });

        return tarjeta;
    }

    private static class TareaData {
        final String texto;
        final String deadline;
        boolean completada;

        TareaData(String texto, String deadline) {
            this.texto = texto;
            this.deadline = deadline;
            this.completada = false;
        }
    }

    private static void paintRoundRect(Graphics g, int w, int h, Color color, int radio) {
        ConstantesUI.paintRoundRect(g, w, h, color, radio);
    }
}

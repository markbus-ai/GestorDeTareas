package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class CalendarioMensual extends JPanel {

    private YearMonth mesActual = YearMonth.now();

    private LocalDate fechaSeleccionada = LocalDate.now();

    private final Map<LocalDate, List<String>> recordatorios = new HashMap<>();

    private JLabel labelMesAnio;
    private JPanel panelDias;

    private Consumer<LocalDate> onFechaSeleccionada;

    public CalendarioMensual() {
        setLayout(new BorderLayout());
        setOpaque(false);

        panelDias = new JPanel();

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(crearNombresDias(), BorderLayout.NORTH);
        contenedor.add(panelDias, BorderLayout.CENTER);

        add(crearCabecera(), BorderLayout.NORTH);
        add(contenedor, BorderLayout.CENTER);

        dibujarDias();

        ThemeManager.onThemeChange(() -> {
            repaint();
            revalidate();
        });
    }

    public void setOnFechaSeleccionada(Consumer<LocalDate> listener) {
        this.onFechaSeleccionada = listener;
    }

    public void agregarRecordatorio(LocalDate fecha, String texto) {
        recordatorios.computeIfAbsent(fecha, f -> new ArrayList<>()).add(texto);
        repaint();
    }

    public void borrarRecordatorio(LocalDate fecha, String texto) {
        List<String> lista = recordatorios.get(fecha);
        if (lista != null) {
            lista.remove(texto);
            repaint();
        }
    }

    public List<String> obtenerRecordatorios(LocalDate fecha) {
        return recordatorios.getOrDefault(fecha, new ArrayList<>());
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JButton flechaAtras = new JButton("<");
        JButton flechaAdelante = new JButton(">");

        for (JButton flecha : new JButton[]{flechaAtras, flechaAdelante}) {
            flecha.setFont(new Font("Segoe UI", Font.BOLD, 15));
            flecha.setForeground(ThemeManager.textoSuave());
            flecha.setBackground(ThemeManager.tarjeta());
            flecha.setFocusPainted(false);
            flecha.setPreferredSize(new Dimension(34, 34));
            flecha.putClientProperty("JButton.buttonType", "roundRect");
            flecha.putClientProperty("JButton.arc", 999);
            flecha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        flechaAtras.addActionListener(e -> { mesActual = mesActual.minusMonths(1); dibujarDias(); revalidate(); repaint(); });
        flechaAdelante.addActionListener(e -> { mesActual = mesActual.plusMonths(1); dibujarDias(); revalidate(); repaint(); });

        labelMesAnio = new JLabel("", SwingConstants.CENTER);
        labelMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelMesAnio.setForeground(ThemeManager.texto());

        cabecera.add(flechaAtras, BorderLayout.WEST);
        cabecera.add(labelMesAnio, BorderLayout.CENTER);
        cabecera.add(flechaAdelante, BorderLayout.EAST);

        ThemeManager.onThemeChange(() -> {
            for (JButton f : new JButton[]{flechaAtras, flechaAdelante}) {
                f.setForeground(ThemeManager.textoSuave());
                f.setBackground(ThemeManager.tarjeta());
            }
            labelMesAnio.setForeground(ThemeManager.texto());
        });

        return cabecera;
    }

    private JPanel crearNombresDias() {
        JPanel fila = new JPanel(new GridLayout(1, 7, 4, 4));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] dias = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

        for (String dia : dias) {
            JLabel label = new JLabel(dia, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setForeground(ThemeManager.textoSuave());
            fila.add(label);
        }

        return fila;
    }

    private void dibujarDias() {

        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
        labelMesAnio.setText(nombreMes + " " + mesActual.getYear());

        panelDias.removeAll();
        panelDias.setLayout(new GridLayout(0, 7, 4, 8));
        panelDias.setOpaque(false);

        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int huecosAntes = primerDiaDelMes.getDayOfWeek().getValue() - 1;

        for (int i = 0; i < huecosAntes; i++) {
            panelDias.add(crearCeldaVacia());
        }

        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            panelDias.add(crearCeldaDia(fecha));
        }
    }

    private JPanel crearCeldaVacia() {
        JPanel celda = new JPanel();
        celda.setOpaque(false);
        celda.setPreferredSize(new Dimension(56, 56));
        return celda;
    }

    private JPanel crearCeldaDia(LocalDate fecha) {
        JPanel celda = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                boolean esHoy = fecha.equals(LocalDate.now());
                boolean esSeleccionado = fecha.equals(fechaSeleccionada);
                int diametro = 36;
                int x = (getWidth() - diametro) / 2;
                int y = (getHeight() - diametro) / 2;

                if (esHoy) {
                    g2.setColor(ThemeManager.AZUL);
                    g2.fillOval(x, y, diametro, diametro);
                } else if (esSeleccionado) {
                    g2.setColor(ThemeManager.AZUL_SUAVE);
                    g2.fillOval(x, y, diametro, diametro);
                }

                if (!obtenerRecordatorios(fecha).isEmpty()) {
                    g2.setColor(ThemeManager.NARANJA);
                    g2.fillOval(getWidth() / 2 - 3, y + diametro + 1, 6, 6);
                }

                g2.dispose();
            }
        };

        celda.setOpaque(false);
        celda.setPreferredSize(new Dimension(56, 56));
        celda.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean esHoy = fecha.equals(LocalDate.now());
        JLabel numero = new JLabel(String.valueOf(fecha.getDayOfMonth()));
        numero.setFont(new Font("Segoe UI", esHoy ? Font.BOLD : Font.PLAIN, 13));
        numero.setForeground(esHoy ? Color.WHITE : ThemeManager.texto());
        celda.add(numero);

        ThemeManager.onThemeChange(() -> {
            boolean ahoraHoy = fecha.equals(LocalDate.now());
            numero.setForeground(ahoraHoy ? Color.WHITE : ThemeManager.texto());
            celda.repaint();
        });

        celda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fechaSeleccionada = fecha;
                repaint();
                if (onFechaSeleccionada != null) onFechaSeleccionada.accept(fecha);
            }
        });

        return celda;
    }
}

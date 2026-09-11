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

// el calendario de toda la vida pero hecho a mano
// muestra el mes, podes ir para atras/adelante y tocar un dia
// tambien guarda que dias tienen recordatorios (puntito naranja)
public class CalendarioMensual extends JPanel {

    // que mes estoy viendo ahora
    private YearMonth mesActual = YearMonth.now();

    // que dia tengo seleccionado (arranca hoy)
    private LocalDate fechaSeleccionada = LocalDate.now();

    // aca guardo los recordatorios, clave es la fecha y valor la lista de textos
    private final Map<LocalDate, List<String>> recordatorios = new HashMap<>();

    // labels y paneles que necesito tocar despues
    private JLabel labelMesAnio;
    private JPanel panelDias;

    // funcion que me avisa cuando tocan una fecha, la setea el panel de recordatorios
    private Consumer<LocalDate> onFechaSeleccionada;

    // armo todo el calendario
    public CalendarioMensual() {
        setLayout(new BorderLayout());
        setOpaque(false);

        panelDias = new JPanel(); // aca van los numeritos despues

        // contenedor con los nombres de dias arriba y los dias abajo
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(crearNombresDias(), BorderLayout.NORTH);
        contenedor.add(panelDias, BorderLayout.CENTER);

        add(crearCabecera(), BorderLayout.NORTH); // flechas y mes
        add(contenedor, BorderLayout.CENTER);

        dibujarDias(); // primera pintada

        // si cambia el tema repinto
        ThemeManager.onThemeChange(() -> {
            repaint();
            revalidate();
        });
    }

    // para que otro panel se suscriba a cuando tocan una fecha
    public void setOnFechaSeleccionada(Consumer<LocalDate> listener) {
        this.onFechaSeleccionada = listener;
    }

    // le agrego un recordatorio a una fecha
    public void agregarRecordatorio(LocalDate fecha, String texto) {
        recordatorios.computeIfAbsent(fecha, f -> new ArrayList<>()).add(texto);
        repaint(); // para que se vea el puntito
    }

    // borro un recordatorio
    public void borrarRecordatorio(LocalDate fecha, String texto) {
        List<String> lista = recordatorios.get(fecha);
        if (lista != null) {
            lista.remove(texto);
            repaint();
        }
    }

    // dame los recordatorios de una fecha, si no hay nada devuelvo lista vacia
    public List<String> obtenerRecordatorios(LocalDate fecha) {
        return recordatorios.getOrDefault(fecha, new ArrayList<>());
    }

    // la parte de arriba con < mes año >
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JButton flechaAtras = new JButton("<");
        JButton flechaAdelante = new JButton(">");

        // les doy estilo a las dos flechas
        for (JButton flecha : new JButton[]{flechaAtras, flechaAdelante}) {
            flecha.setFont(new Font("Segoe UI", Font.BOLD, 15));
            flecha.setForeground(ThemeManager.textoSuave());
            flecha.setBackground(ThemeManager.tarjeta());
            flecha.setFocusPainted(false);
            flecha.setPreferredSize(new Dimension(34, 34));
            flecha.putClientProperty("JButton.buttonType", "roundRect");
            flecha.putClientProperty("JButton.arc", 999); // bien redonda
            flecha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // al tocar cambian el mes y redibujan
        flechaAtras.addActionListener(e -> { mesActual = mesActual.minusMonths(1); dibujarDias(); revalidate(); repaint(); });
        flechaAdelante.addActionListener(e -> { mesActual = mesActual.plusMonths(1); dibujarDias(); revalidate(); repaint(); });

        labelMesAnio = new JLabel("", SwingConstants.CENTER);
        labelMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelMesAnio.setForeground(ThemeManager.texto());

        cabecera.add(flechaAtras, BorderLayout.WEST);
        cabecera.add(labelMesAnio, BorderLayout.CENTER);
        cabecera.add(flechaAdelante, BorderLayout.EAST);

        // tema cambia -> actualizo colores
        ThemeManager.onThemeChange(() -> {
            for (JButton f : new JButton[]{flechaAtras, flechaAdelante}) {
                f.setForeground(ThemeManager.textoSuave());
                f.setBackground(ThemeManager.tarjeta());
            }
            labelMesAnio.setForeground(ThemeManager.texto());
        });

        return cabecera;
    }

    // la filita con Lun Mar Mie etc
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

    // aca dibujo todos los dias del mes, la parte mas jodida
    private void dibujarDias() {
        // nombre del mes en español con mayuscula
        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
        labelMesAnio.setText(nombreMes + " " + mesActual.getYear());

        panelDias.removeAll();
        panelDias.setLayout(new GridLayout(0, 7, 4, 8));
        panelDias.setOpaque(false);

        // si el mes no empieza lunes tengo que dejar huecos vacios
        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int huecosAntes = primerDiaDelMes.getDayOfWeek().getValue() - 1;

        for (int i = 0; i < huecosAntes; i++) {
            panelDias.add(crearCeldaVacia());
        }

        // ahora si, cada dia del mes
        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            panelDias.add(crearCeldaDia(fecha));
        }
    }

    // celdita vacia para los huecos del principio
    private JPanel crearCeldaVacia() {
        JPanel celda = new JPanel();
        celda.setOpaque(false);
        celda.setPreferredSize(new Dimension(56, 56));
        return celda;
    }

    // cada numerito del calendario, con su circulo si es hoy/seleccionado y puntito si tiene recordatorio
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

                // si es hoy va azul fuerte, si es seleccionado pero no hoy va azul clarito
                if (esHoy) {
                    g2.setColor(ThemeManager.AZUL);
                    g2.fillOval(x, y, diametro, diametro);
                } else if (esSeleccionado) {
                    g2.setColor(ThemeManager.AZUL_SUAVE);
                    g2.fillOval(x, y, diametro, diametro);
                }

                // puntito naranja abajo si tiene recordatorios
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

        // actualizo color si cambia tema
        ThemeManager.onThemeChange(() -> {
            boolean ahoraHoy = fecha.equals(LocalDate.now());
            numero.setForeground(ahoraHoy ? Color.WHITE : ThemeManager.texto());
            celda.repaint();
        });

        // click en el dia
        celda.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fechaSeleccionada = fecha;
                repaint(); // repinto todo para que se mueva el circulito
                if (onFechaSeleccionada != null) onFechaSeleccionada.accept(fecha);
            }
        });

        return celda;
    }
}

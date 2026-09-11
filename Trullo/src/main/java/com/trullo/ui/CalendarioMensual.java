package com.trullo.ui;

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

// Este es el calendario mensual, muestra los dias del mes actual
// permite navegar entre meses con flechas y seleccionar dias
// tambien maneja los recordatorios asociados a cada fecha
public class CalendarioMensual extends JPanel {

    // el mes y año actual que se esta mostrando
    private YearMonth mesActual = YearMonth.now();

    // la fecha que esta seleccionada actualmente
    private LocalDate fechaSeleccionada = LocalDate.now();

    // mapa de recordatorios: cada fecha tiene una lista de textos
    private final Map<LocalDate, List<String>> recordatorios = new HashMap<>();

    // label que muestra el mes y año
    private JLabel labelMesAnio;

    // panel donde se dibujan los dias del mes
    private JPanel panelDias;

    // callback que se ejecuta cuando seleccionan una fecha
    private Consumer<LocalDate> onFechaSeleccionada;

    // constructor, arma toda la interfaz del calendario
    public CalendarioMensual() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // panel de dias, se llena dinamicamente
        panelDias = new JPanel();

        // contenedor que tiene los nombres de los dias (Lun, Mar, etc) y los dias
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.add(crearNombresDias(), BorderLayout.NORTH);
        contenedor.add(panelDias, BorderLayout.CENTER);

        // cabecera con las flechas de navegacion y el mes/año
        add(crearCabecera(), BorderLayout.NORTH);
        add(contenedor, BorderLayout.CENTER);

        dibujarDias(); // dibujamos los dias por primera vez

        // listener para cambio de tema
        ThemeManager.onThemeChange(() -> {
            repaint();
            revalidate();
        });
    }

    // setter para el callback de fecha seleccionada
    public void setOnFechaSeleccionada(Consumer<LocalDate> listener) {
        this.onFechaSeleccionada = listener;
    }

    // agrega un recordatorio a una fecha
    // si la fecha no tiene recordatorios, crea la lista
    public void agregarRecordatorio(LocalDate fecha, String texto) {
        recordatorios.computeIfAbsent(fecha, f -> new ArrayList<>()).add(texto);
        repaint(); // repintamos para que se vea el puntito naranja
    }

    // borra un recordatorio de una fecha
    public void borrarRecordatorio(LocalDate fecha, String texto) {
        List<String> lista = recordatorios.get(fecha);
        if (lista != null) {
            lista.remove(texto);
            repaint();
        }
    }

    // obtiene los recordatorios de una fecha
    // devuelve una lista vacia si no hay nada
    public List<String> obtenerRecordatorios(LocalDate fecha) {
        return recordatorios.getOrDefault(fecha, new ArrayList<>());
    }

    // crea la cabecera con las flechas de navegacion y el label del mes/año
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        // flechas para navegar entre meses
        JButton flechaAtras = new JButton("<");
        JButton flechaAdelante = new JButton(">");

        // estilo de las flechas
        for (JButton flecha : new JButton[]{flechaAtras, flechaAdelante}) {
            flecha.setFont(new Font("Segoe UI", Font.BOLD, 15));
            flecha.setForeground(ThemeManager.textoSuave());
            flecha.setBackground(ThemeManager.tarjeta());
            flecha.setFocusPainted(false);
            flecha.setPreferredSize(new Dimension(34, 34));
            flecha.putClientProperty("JButton.buttonType", "roundRect");
            flecha.putClientProperty("JButton.arc", 999); // redondas como bolitas
            flecha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // acciones de las flechas: restar o sumar un mes
        flechaAtras.addActionListener(e -> { mesActual = mesActual.minusMonths(1); dibujarDias(); revalidate(); repaint(); });
        flechaAdelante.addActionListener(e -> { mesActual = mesActual.plusMonths(1); dibujarDias(); revalidate(); repaint(); });

        // label del mes y año, centrado
        labelMesAnio = new JLabel("", SwingConstants.CENTER);
        labelMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelMesAnio.setForeground(ThemeManager.texto());

        // acomodamos: flecha atras a la izq, mes al centro, flecha adelante a la der
        cabecera.add(flechaAtras, BorderLayout.WEST);
        cabecera.add(labelMesAnio, BorderLayout.CENTER);
        cabecera.add(flechaAdelante, BorderLayout.EAST);

        // listener para cambio de tema
        ThemeManager.onThemeChange(() -> {
            for (JButton f : new JButton[]{flechaAtras, flechaAdelante}) {
                f.setForeground(ThemeManager.textoSuave());
                f.setBackground(ThemeManager.tarjeta());
            }
            labelMesAnio.setForeground(ThemeManager.texto());
        });

        return cabecera;
    }

    // crea la fila con los nombres de los dias (Lun, Mar, Mie, etc)
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

    // dibuja los dias del mes actual en el panel
    // calcula los huecos al principio si el mes no empieza en lunes
    private void dibujarDias() {
        // formateamos el nombre del mes en español
        String nombreMes = mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);
        labelMesAnio.setText(nombreMes + " " + mesActual.getYear());

        panelDias.removeAll();
        panelDias.setLayout(new GridLayout(0, 7, 4, 8));
        panelDias.setOpaque(false);

        // calculamos cuantos huecos necesitamos al principio
        // (porque si el mes empieza en miercoles, lunes y martes van vacios)
        LocalDate primerDiaDelMes = mesActual.atDay(1);
        int huecosAntes = primerDiaDelMes.getDayOfWeek().getValue() - 1;

        // agregamos las celdas vacias al principio
        for (int i = 0; i < huecosAntes; i++) {
            panelDias.add(crearCeldaVacia());
        }

        // agregamos las celdas de cada dia del mes
        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            panelDias.add(crearCeldaDia(fecha));
        }
    }

    // crea una celda vacia (para los huecos antes del dia 1)
    private JPanel crearCeldaVacia() {
        JPanel celda = new JPanel();
        celda.setOpaque(false);
        celda.setPreferredSize(new Dimension(56, 56));
        return celda;
    }

    // crea una celda para un dia del mes
    // tiene el numero del dia, y si es hoy o esta seleccionado, tiene un circulo de fondo
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

                // si es hoy, dibujamos un circulo azul solido
                if (esHoy) {
                    g2.setColor(ThemeManager.AZUL);
                    g2.fillOval(x, y, diametro, diametro);
                } else if (esSeleccionado) {
                    // si esta seleccionado pero no es hoy, circulo azul suave
                    g2.setColor(ThemeManager.AZUL_SUAVE);
                    g2.fillOval(x, y, diametro, diametro);
                }

                // si tiene recordatorios, dibujamos un puntito naranja abajo del dia
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

        // el numero del dia
        boolean esHoy = fecha.equals(LocalDate.now());
        JLabel numero = new JLabel(String.valueOf(fecha.getDayOfMonth()));
        numero.setFont(new Font("Segoe UI", esHoy ? Font.BOLD : Font.PLAIN, 13));
        numero.setForeground(esHoy ? Color.WHITE : ThemeManager.texto());
        celda.add(numero);

        // cuando cambia el tema, actualizamos el color del numero
        ThemeManager.onThemeChange(() -> {
            boolean ahoraHoy = fecha.equals(LocalDate.now());
            numero.setForeground(ahoraHoy ? Color.WHITE : ThemeManager.texto());
            celda.repaint();
        });

        // al hacer click en un dia, lo seleccionamos
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

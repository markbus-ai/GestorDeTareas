package com.trullo.frontend;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

// panel de notitas tipo post-it: cada una es una card de color que se expande al tocarla
public class PanelNotas extends JPanel {

    // donde van las filas de tarjetitas
    private final JPanel listaNotas;

    // los datos posta, separados de lo visual
    private final List<NotaData> notas = new ArrayList<>();

    // el buscador de arriba que filtra al tipear
    private final JTextField campoFiltro;

    private final JLabel titulo;

    // para que no sean todas iguales, el color va rotando
    private static final Color[] COLORES_NOTA = {
        new Color(62, 85, 105),
        new Color(72, 95, 78),
        new Color(95, 78, 68),
        new Color(80, 68, 95),
        new Color(68, 85, 98),
    };

    public PanelNotas() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));
        setBackground(ThemeManager.fondo());

        // título de la sección
        titulo = new JLabel("Notas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());

        campoFiltro = new JTextField();
        campoFiltro.putClientProperty("JTextField.placeholderText", "Buscar nota...");
        campoFiltro.putClientProperty("JTextField.showClearButton", true);
        campoFiltro.putClientProperty("JComponent.arc", 12);
        campoFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoFiltro.setBackground(ThemeManager.input());
        campoFiltro.setForeground(ThemeManager.texto());
        campoFiltro.setCaretColor(ThemeManager.texto());
        campoFiltro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                new EmptyBorder(8, 12, 8, 12)));
        campoFiltro.setPreferredSize(new Dimension(0, 36));

        // buscador a lo ancho con aire abajo
        JPanel panelFiltro = new JPanel(new BorderLayout());
        panelFiltro.setOpaque(false);
        panelFiltro.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        panelFiltro.add(campoFiltro, BorderLayout.CENTER);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));
        panelSuperior.setOpaque(false);
        panelSuperior.add(titulo);
        panelSuperior.add(Box.createVerticalStrut(14));
        panelSuperior.add(panelFiltro);

        // filas verticales de a 3: el FlowLayout solo adentro del scroll no hace wrap y queda todo en una línea
        listaNotas = new JPanel();
        listaNotas.setLayout(new BoxLayout(listaNotas, BoxLayout.Y_AXIS));
        listaNotas.setOpaque(false);

        // scroll vertical por si hay muchas filas
        JScrollPane scroll = new JScrollPane(listaNotas);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        // botón redondo verde con el +
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
        botonAgregar.setBackground(ThemeManager.VERDE);
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

        botonAgregar.addActionListener(e -> mostrarDialogoNuevaNota());

        // filtra al tipear, sin botón de buscar
        campoFiltro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refrescarLista(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refrescarLista(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refrescarLista(); }
        });

        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            campoFiltro.setBackground(ThemeManager.input());
            campoFiltro.setForeground(ThemeManager.texto());
            campoFiltro.setCaretColor(ThemeManager.texto());
            campoFiltro.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                    new EmptyBorder(8, 12, 8, 12)));
            botonAgregar.setBackground(ThemeManager.VERDE);
            refrescarLista();
            revalidate();
            repaint();
        });
    }

    // filtra por el buscador y rearma las filas de a 3
    private void refrescarLista() {
        String filtro = campoFiltro.getText().trim().toLowerCase();
        listaNotas.removeAll();

        final int columnas = 3;
        final int anchoCard = 300;
        final int gap = 16;

        List<NotaData> visibles = new ArrayList<>();
        for (NotaData nota : notas) {
            boolean mostrar = filtro.isEmpty() ||
                    nota.titulo.toLowerCase().contains(filtro) ||
                    (nota.contenido != null && nota.contenido.toLowerCase().contains(filtro));
            if (mostrar) visibles.add(nota);
        }

        if (visibles.isEmpty()) {
            JLabel vacio = new JLabel("No hay notas", SwingConstants.LEFT);
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vacio.setForeground(ThemeManager.textoSuave());
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaNotas.add(vacio);
        } else {
            int idx = 0;
            for (int desde = 0; desde < visibles.size(); desde += columnas) {
                JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, gap, 8));
                fila.setOpaque(false);
                fila.setAlignmentX(Component.LEFT_ALIGNMENT);
                for (int i = desde; i < Math.min(desde + columnas, visibles.size()); i++) {
                    fila.add(crearTarjetaNota(visibles.get(i), idx, anchoCard));
                    idx++;
                }
                // tope de alto para que la fila no se estire cuando sobra lugar
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, fila.getPreferredSize().height));
                listaNotas.add(fila);
            }
        }

        listaNotas.revalidate();
        listaNotas.repaint();
    }

    // armo una tarjetita de color con título visible y contenido que se abre al tocar
    private JPanel crearTarjetaNota(NotaData nota, int indice, int anchoCard) {
        CardState state = new CardState();
        Color colorBase = COLORES_NOTA[indice % COLORES_NOTA.length];
        final Color[] colorActual = {colorBase};

        int altoFijo = 200;

        // fondo con sombrita abajo y brillo arriba para que no quede plana
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(3, 3, getWidth(), getHeight(), 16, 16);
                g2.setColor(colorActual[0]);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRect(0, 0, getWidth(), 3);
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        // layout a mano porque los pibes van en posiciones fijas
        tarjeta.setLayout(null);
        tarjeta.setPreferredSize(new Dimension(anchoCard, altoFijo));
        tarjeta.setMaximumSize(new Dimension(anchoCard, altoFijo));
        tarjeta.setMinimumSize(new Dimension(anchoCard, altoFijo));

        // título siempre visible arriba
        JLabel labelTitulo = new JLabel(nota.titulo, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setBounds(18, 14, anchoCard - 70, 24);

        // flechita que apunta para donde se va a expandir
        JLabel labelFlecha = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(), h = getHeight();
                int cx = w / 2, cy = h / 2;
                if (state.expandida) {
                    g2.drawLine(cx - 6, cy + 3, cx, cy - 3);
                    g2.drawLine(cx, cy - 3, cx + 6, cy + 3);
                } else {
                    g2.drawLine(cx - 6, cy - 3, cx, cy + 3);
                    g2.drawLine(cx, cy + 3, cx + 6, cy - 3);
                }
                g2.dispose();
            }
        };
        labelFlecha.setBounds(anchoCard - 60, 14, 20, 20);

        // la x para borrar, dibujada a mano
        JLabel botonBorrar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(), h = getHeight();
                int m = 5;
                g2.drawLine(m, m, w - m, h - m);
                g2.drawLine(w - m, m, m, h - m);
                g2.dispose();
            }
        };
        botonBorrar.setForeground(new Color(255, 255, 255, 140));
        botonBorrar.setBounds(anchoCard - 34, 14, 20, 20);
        botonBorrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // linecita entre el título y el contenido
        JLabel separador = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRect(18, 0, getWidth() - 36, 1);
                g2.dispose();
            }
        };
        separador.setBounds(0, 44, anchoCard, 1);

        // el contenido arranca oculto y se muestra al expandir
        JTextArea labelContenido = new JTextArea();
        labelContenido.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelContenido.setForeground(new Color(255, 255, 255, 210));
        labelContenido.setBackground(new Color(0, 0, 0, 0));
        labelContenido.setEditable(false);
        labelContenido.setLineWrap(true);
        labelContenido.setWrapStyleWord(true);
        labelContenido.setBounds(14, 52, anchoCard - 28, altoFijo - 62);
        labelContenido.setVisible(false);

        tarjeta.add(labelTitulo);
        tarjeta.add(labelFlecha);
        tarjeta.add(botonBorrar);
        tarjeta.add(separador);
        tarjeta.add(labelContenido);

        // hover: se ilumina un toque
        tarjeta.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                colorActual[0] = colorBase.brighter();
                tarjeta.repaint();
            }
            public void mouseExited(MouseEvent e) {
                colorActual[0] = colorBase;
                botonBorrar.setForeground(new Color(255, 255, 255, 140));
                tarjeta.repaint();
            }
        });

        // la x se pone rojita en hover
        botonBorrar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonBorrar.setForeground(new Color(255, 90, 90)); }
            public void mouseExited(MouseEvent e) { botonBorrar.setForeground(new Color(255, 255, 255, 140)); }
        });

        // click en la x borra sin expandir, el consume es para que no le llegue a la tarjeta
        botonBorrar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                e.consume();
                notas.remove(nota);
                refrescarLista();
            }
        });

        // click en la tarjeta expande o colapsa
        tarjeta.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == botonBorrar) return;
                state.expandida = !state.expandida;
                labelFlecha.repaint();
                if (state.expandida) {
                    String txt = (nota.contenido != null && !nota.contenido.isEmpty())
                            ? nota.contenido : "(sin contenido)";
                    labelContenido.setText(txt);
                    labelContenido.setVisible(true);
                } else {
                    labelContenido.setVisible(false);
                    labelContenido.setText("");
                }
                tarjeta.repaint();
            }
        });

        return tarjeta;
    }

    // diálogo para crear una nota con título y contenido
    private void mostrarDialogoNuevaNota() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva nota", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(580, 520);
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

        JLabel labelHeader = new JLabel("Nueva nota");
        labelHeader.setFont(new Font("Segoe UI", Font.BOLD, 17));
        labelHeader.setForeground(ThemeManager.texto());

        header.add(labelHeader, BorderLayout.WEST);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 28, 16, 28));

        JLabel labelTitulo = new JLabel("Titulo");
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTitulo.setForeground(ThemeManager.texto());
        labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField campoTitulo = new JTextField();
        campoTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoTitulo.putClientProperty("JTextField.placeholderText", "Escribi el titulo...");
        campoTitulo.putClientProperty("JComponent.arc", 12);
        campoTitulo.setBackground(ThemeManager.input());
        campoTitulo.setForeground(ThemeManager.texto());
        campoTitulo.setCaretColor(ThemeManager.texto());
        campoTitulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.borde(), 1),
                new EmptyBorder(10, 14, 10, 14)));
        campoTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campoTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelContenido = new JLabel("Contenido");
        labelContenido.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelContenido.setForeground(ThemeManager.texto());
        labelContenido.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea areaContenido = new JTextArea();
        areaContenido.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        areaContenido.setLineWrap(true);
        areaContenido.setWrapStyleWord(true);
        areaContenido.setBackground(ThemeManager.input());
        areaContenido.setForeground(ThemeManager.texto());
        areaContenido.setCaretColor(ThemeManager.texto());
        areaContenido.setBorder(new EmptyBorder(10, 14, 10, 14));

        JScrollPane scrollArea = new JScrollPane(areaContenido);
        scrollArea.setBorder(BorderFactory.createLineBorder(ThemeManager.borde(), 1));
        scrollArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollArea.setPreferredSize(new Dimension(0, 220));
        scrollArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        scrollArea.getVerticalScrollBar().setUI(new ModernScrollBarUI());

        contenido.add(labelTitulo);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(campoTitulo);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(labelContenido);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(scrollArea);

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

        JButton botonCrear = new JButton("Crear nota");
        botonCrear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botonCrear.setForeground(Color.WHITE);
        botonCrear.setBackground(ThemeManager.VERDE);
        botonCrear.setBorderPainted(false);
        botonCrear.setFocusPainted(false);
        botonCrear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonCrear.setPreferredSize(new Dimension(110, 36));
        botonCrear.addActionListener(e -> {
            String tituloNota = campoTitulo.getText().trim();
            String contenidoNota = areaContenido.getText().trim();
            if (!tituloNota.isEmpty()) {
                agregarNota(tituloNota, contenidoNota);
                dialog.dispose();
            } else {
                campoTitulo.requestFocus();
                campoTitulo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.ROSA, 2),
                        new EmptyBorder(10, 14, 10, 14)));
            }
        });

        panelPrincipal.registerKeyboardAction(e -> botonCrear.doClick(),
                KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        footer.add(botonCancelar);
        footer.add(botonCrear);

        panelPrincipal.add(header, BorderLayout.NORTH);
        panelPrincipal.add(contenido, BorderLayout.CENTER);
        panelPrincipal.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(panelPrincipal);
        dialog.getRootPane().setDefaultButton(botonCrear);
        dialog.setVisible(true);
    }

    // agrega la nota y refresca la grilla
    public void agregarNota(String titulo, String contenido) {
        notas.add(new NotaData(titulo, contenido));
        refrescarLista();
    }

    // si la card está abierta o no
    private static class CardState { boolean expandida = false; }

    // datitos de la nota, sin nada visual
    private static class NotaData {
        final String titulo;
        final String contenido;
        NotaData(String titulo, String contenido) {
            this.titulo = titulo;
            this.contenido = contenido;
        }
    }
}

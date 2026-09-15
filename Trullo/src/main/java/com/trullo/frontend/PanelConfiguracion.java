package com.trullo.frontend;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelConfiguracion extends JPanel {

    private JLabel titulo;
    private JLabel labelIcono;
    private JLabel labelTexto;
    private JLabel labelVersion;

    private JPanel cardTema;
    private JPanel cardIdioma;
    private JPanel cardNotificacion;
    private JPanel cardTerminos;
    private JPanel cardCreadores;
    private JPanel separator;
    private JScrollPane scrollPane;
    private JPanel contenidoRef;

    private JToggleButton toggle;
    private JComboBox<String> comboIdioma;
    private JComboBox<String> comboNotificacion;
    private JPanel panelNotiInputWrapper;
    private JTextField campoNotificacion;
    private JLabel labelCampoNoti;

    private JPanel panelTerminosContent;
    private JPanel panelCreadoresContent;
    private JLabel chevronTerminos;
    private JLabel chevronCreadores;
    private boolean terminosExpandido = false;
    private boolean creadoresExpandido = false;

    public PanelConfiguracion() {
        inicializar();
        configurarActualizacionTema();
    }

    private void inicializar() {

        setLayout(new BorderLayout());
        setBackground(ThemeManager.fondo());

        contenidoRef = new JPanel();
        contenidoRef.setLayout(new BoxLayout(contenidoRef, BoxLayout.Y_AXIS));
        contenidoRef.setBackground(ThemeManager.fondo());
        contenidoRef.setBorder(
                BorderFactory.createEmptyBorder(
                        36,
                        48,
                        36,
                        48
                )
        );
        JPanel contenido = contenidoRef;

        titulo = new JLabel("Configuración");

        titulo.setFont(
                new Font("Segoe UI", Font.BOLD, 24)
        );

        titulo.setForeground(ThemeManager.texto());
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardTema = new JPanel(new BorderLayout(16, 0)) {

            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(
                        g,
                        getWidth(),
                        getHeight(),
                        ThemeManager.tarjeta(),
                        ConstantesUI.RADIO
                );
            }
        };

        cardTema.setOpaque(false);

        cardTema.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        20,
                        16,
                        20
                )
        );

        cardTema.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        64
                )
        );

        cardTema.setAlignmentX(Component.LEFT_ALIGNMENT);

        labelIcono = new JLabel() {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(ThemeManager.AZUL);
                g2.fillOval(2, 2, 16, 16);

                g2.setColor(ThemeManager.tarjeta());
                g2.fillOval(7, 2, 16, 16);

                g2.dispose();
            }
        };

        labelIcono.setPreferredSize(
                new Dimension(20, 20)
        );

        labelTexto = new JLabel(
                "Modo de apariencia"
        );

        labelTexto.setFont(
                new Font("Segoe UI", Font.PLAIN, 15)
        );

        labelTexto.setForeground(
                ThemeManager.texto()
        );

        JPanel info = new JPanel(
                new BorderLayout()
        );

        info.setOpaque(false);
        info.add(
                labelTexto,
                BorderLayout.CENTER
        );

        toggle = new JToggleButton();

        toggle.setOpaque(true);

        toggle.setPreferredSize(
                new Dimension(52, 28)
        );

        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setHorizontalAlignment(SwingConstants.CENTER);

        toggle.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        toggle.putClientProperty(
                "JButton.arc",
                999
        );

        actualizarToggle();

        toggle.addActionListener(e -> {

            ThemeManager.toggle();

            actualizarToggle();
        });

        cardTema.add(
                labelIcono,
                BorderLayout.WEST
        );

        cardTema.add(
                info,
                BorderLayout.CENTER
        );

        cardTema.add(
                toggle,
                BorderLayout.EAST
        );

        cardIdioma = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardIdioma.setOpaque(false);
        cardIdioma.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardIdioma.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        cardIdioma.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconoIdioma = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.CELESTE);
                g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(2, 2, 16, 16);
                g2.drawLine(10, 2, 10, 18);
                g2.drawLine(2, 10, 18, 10);

                g2.drawArc(5, 2, 10, 16, 90, 180);
                g2.drawArc(5, 2, 10, 16, 270, 180);
                g2.dispose();
            }
        };
        iconoIdioma.setPreferredSize(new Dimension(20, 20));

        JLabel labelIdioma = new JLabel("Idioma");
        labelIdioma.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelIdioma.setForeground(ThemeManager.texto());

        JPanel infoIdioma = new JPanel(new BorderLayout());
        infoIdioma.setOpaque(false);
        infoIdioma.add(labelIdioma, BorderLayout.CENTER);

        comboIdioma = new JComboBox<>(new String[]{"Español (ES)", "Português (PT)", "English (EN)"});
        comboIdioma.setSelectedIndex(0);
        estilizarCombo(comboIdioma);
        comboIdioma.setPreferredSize(new Dimension(170, 32));
        comboIdioma.addActionListener(e -> {
            String sel = (String) comboIdioma.getSelectedItem();
            comboIdioma.setToolTipText("Idioma seleccionado: " + sel);
        });

        cardIdioma.add(iconoIdioma, BorderLayout.WEST);
        cardIdioma.add(infoIdioma, BorderLayout.CENTER);
        cardIdioma.add(comboIdioma, BorderLayout.EAST);

        cardNotificacion = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardNotificacion.setOpaque(false);
        cardNotificacion.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardNotificacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        cardNotificacion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerNoti = new JPanel(new BorderLayout(16, 0));
        headerNoti.setOpaque(false);

        JLabel iconoNoti = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.NARANJA);
                g2.fillArc(3, 2, 14, 14, 0, 180);
                g2.fillRect(3, 9, 14, 6);
                g2.setColor(ThemeManager.NARANJA.darker());
                g2.fillOval(8, 15, 4, 4);
                g2.setColor(Color.WHITE);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                g2.fillOval(5, 4, 4, 4);
                g2.dispose();
            }
        };
        iconoNoti.setPreferredSize(new Dimension(20, 20));

        JLabel labelNoti = new JLabel("Notificaciones");
        labelNoti.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelNoti.setForeground(ThemeManager.texto());

        JPanel infoNoti = new JPanel(new BorderLayout());
        infoNoti.setOpaque(false);
        infoNoti.add(labelNoti, BorderLayout.CENTER);

        comboNotificacion = new JComboBox<>(new String[]{"No notificar", "WhatsApp", "Email"});
        estilizarCombo(comboNotificacion);
        comboNotificacion.setPreferredSize(new Dimension(150, 32));

        headerNoti.add(iconoNoti, BorderLayout.WEST);
        headerNoti.add(infoNoti, BorderLayout.CENTER);
        headerNoti.add(comboNotificacion, BorderLayout.EAST);

        panelNotiInputWrapper = new JPanel(new BorderLayout(10, 0));
        panelNotiInputWrapper.setOpaque(false);
        panelNotiInputWrapper.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        panelNotiInputWrapper.setVisible(false);

        labelCampoNoti = new JLabel("Teléfono");
        labelCampoNoti.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelCampoNoti.setForeground(ThemeManager.textoSuave());
        labelCampoNoti.setPreferredSize(new Dimension(70, 36));
        labelCampoNoti.setHorizontalAlignment(SwingConstants.LEFT);

        campoNotificacion = new JTextField();
        campoNotificacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoNotificacion.setForeground(ThemeManager.texto());
        campoNotificacion.setCaretColor(ThemeManager.texto());
        campoNotificacion.setBackground(ThemeManager.input());
        campoNotificacion.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.borde(), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campoNotificacion.putClientProperty("JTextField.arc", ConstantesUI.RADIO);

        panelNotiInputWrapper.add(labelCampoNoti, BorderLayout.WEST);
        panelNotiInputWrapper.add(campoNotificacion, BorderLayout.CENTER);

        comboNotificacion.addActionListener(e -> actualizarCampoNotificacion());

        cardNotificacion.add(headerNoti, BorderLayout.NORTH);
        cardNotificacion.add(panelNotiInputWrapper, BorderLayout.CENTER);

        cardTerminos = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardTerminos.setOpaque(false);
        cardTerminos.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardTerminos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        cardTerminos.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTerminos.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel headerTerminos = new JPanel(new BorderLayout(16, 0));
        headerTerminos.setOpaque(false);

        JLabel iconoTerminos = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.VERDE);

                int[] xs = {10, 18, 10, 2};
                int[] ys = {2, 7, 18, 7};
                g2.fillPolygon(xs, ys, 4);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(7, 10, 9, 12);
                g2.drawLine(9, 12, 14, 7);
                g2.dispose();
            }
        };
        iconoTerminos.setPreferredSize(new Dimension(20, 20));

        JLabel labelTerminos = new JLabel("Términos de privacidad");
        labelTerminos.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelTerminos.setForeground(ThemeManager.texto());

        chevronTerminos = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.textoSuave());
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int w = getWidth(), h = getHeight();
                if (terminosExpandido) {

                    g2.drawLine(6, 12, 10, 8);
                    g2.drawLine(10, 8, 14, 12);
                } else {

                    g2.drawLine(6, 8, 10, 12);
                    g2.drawLine(10, 12, 14, 8);
                }
                g2.dispose();
            }
        };
        chevronTerminos.setHorizontalAlignment(SwingConstants.CENTER);
        chevronTerminos.setPreferredSize(new Dimension(20, 20));
        chevronTerminos.setOpaque(false);

        JPanel infoTerminos = new JPanel(new BorderLayout());
        infoTerminos.setOpaque(false);
        infoTerminos.add(labelTerminos, BorderLayout.CENTER);

        headerTerminos.add(iconoTerminos, BorderLayout.WEST);
        headerTerminos.add(infoTerminos, BorderLayout.CENTER);
        headerTerminos.add(chevronTerminos, BorderLayout.EAST);

        panelTerminosContent = new JPanel(new BorderLayout());
        panelTerminosContent.setOpaque(false);
        panelTerminosContent.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        panelTerminosContent.setVisible(false);

        JPanel separadorTerminos = new JPanel();
        separadorTerminos.setPreferredSize(new Dimension(1, 1));
        separadorTerminos.setBackground(ThemeManager.borde());
        separadorTerminos.setOpaque(true);

        JTextArea textoTerminos = new JTextArea(
                "Última actualización: 13/09/2026\n\n" +
                "1. Recopilación de datos: Trullo almacena tus tareas, notas y recordatorios localmente. No compartimos tu información con terceros.\n\n" +
                "2. Notificaciones: Si elegís WhatsApp o Email, solo usamos tu contacto para enviarte recordatorios. Podés desactivarlo en cualquier momento.\n\n" +
                "3. Idioma y tema: Tus preferencias se guardan solo en este dispositivo (front-only).\n\n" +
                "4. Seguridad: Tus datos no se envían a servidores externos en esta versión.\n\n" +
                "Al continuar usando Trullo aceptás estos términos. Para dudas: soporte@trullo.app"
        );
        textoTerminos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textoTerminos.setForeground(ThemeManager.textoSuave());
        textoTerminos.setBackground(ThemeManager.tarjeta());
        textoTerminos.setLineWrap(true);
        textoTerminos.setWrapStyleWord(true);
        textoTerminos.setEditable(false);
        textoTerminos.setOpaque(false);
        textoTerminos.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        panelTerminosContent.add(separadorTerminos, BorderLayout.NORTH);
        panelTerminosContent.add(textoTerminos, BorderLayout.CENTER);

        cardTerminos.add(headerTerminos, BorderLayout.NORTH);
        cardTerminos.add(panelTerminosContent, BorderLayout.CENTER);

        MouseAdapter toggleTerminos = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                terminosExpandido = !terminosExpandido;
                panelTerminosContent.setVisible(terminosExpandido);
                chevronTerminos.repaint();
                cardTerminos.setMaximumSize(new Dimension(Integer.MAX_VALUE, terminosExpandido ? 260 : 64));
                revalidate();
                repaint();
            }
        };
        cardTerminos.addMouseListener(toggleTerminos);
        headerTerminos.addMouseListener(toggleTerminos);
        chevronTerminos.addMouseListener(toggleTerminos);

        cardCreadores = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardCreadores.setOpaque(false);
        cardCreadores.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardCreadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        cardCreadores.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardCreadores.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel headerCreadores = new JPanel(new BorderLayout(16, 0));
        headerCreadores.setOpaque(false);

        JLabel iconoCreadores = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(ThemeManager.MORADO);
                g2.fillOval(4, 2, 6, 6);
                g2.fillOval(11, 4, 5, 5);
                g2.fillRoundRect(2, 10, 10, 6, 6, 6);
                g2.fillRoundRect(10, 11, 8, 5, 5, 5);
                g2.dispose();
            }
        };
        iconoCreadores.setPreferredSize(new Dimension(20, 20));

        JLabel labelCreadores = new JLabel("Creadores");
        labelCreadores.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelCreadores.setForeground(ThemeManager.texto());

        chevronCreadores = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.textoSuave());
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (creadoresExpandido) {
                    g2.drawLine(6, 12, 10, 8);
                    g2.drawLine(10, 8, 14, 12);
                } else {
                    g2.drawLine(6, 8, 10, 12);
                    g2.drawLine(10, 12, 14, 8);
                }
                g2.dispose();
            }
        };
        chevronCreadores.setHorizontalAlignment(SwingConstants.CENTER);
        chevronCreadores.setPreferredSize(new Dimension(20, 20));
        chevronCreadores.setOpaque(false);

        JPanel infoCreadores = new JPanel(new BorderLayout());
        infoCreadores.setOpaque(false);
        infoCreadores.add(labelCreadores, BorderLayout.CENTER);

        headerCreadores.add(iconoCreadores, BorderLayout.WEST);
        headerCreadores.add(infoCreadores, BorderLayout.CENTER);
        headerCreadores.add(chevronCreadores, BorderLayout.EAST);

        panelCreadoresContent = new JPanel();
        panelCreadoresContent.setLayout(new BoxLayout(panelCreadoresContent, BoxLayout.Y_AXIS));
        panelCreadoresContent.setOpaque(false);
        panelCreadoresContent.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        panelCreadoresContent.setVisible(false);

        JPanel sepCreadores = new JPanel();
        sepCreadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sepCreadores.setBackground(ThemeManager.borde());
        sepCreadores.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelCreadoresContent.add(sepCreadores);
        panelCreadoresContent.add(Box.createVerticalStrut(12));
        panelCreadoresContent.add(crearFilaCreador("Santiago Vera", "Desarrollador", ThemeManager.AZUL));
        panelCreadoresContent.add(Box.createVerticalStrut(10));
        panelCreadoresContent.add(crearFilaCreador("Marcos Bustos", "Desarrollador", ThemeManager.VERDE));
        panelCreadoresContent.add(Box.createVerticalStrut(10));
        panelCreadoresContent.add(crearFilaCreador("Nahuel Romero", "Desarrollador", ThemeManager.NARANJA));

        cardCreadores.add(headerCreadores, BorderLayout.NORTH);
        cardCreadores.add(panelCreadoresContent, BorderLayout.CENTER);

        MouseAdapter toggleCreadores = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                creadoresExpandido = !creadoresExpandido;
                panelCreadoresContent.setVisible(creadoresExpandido);
                chevronCreadores.repaint();
                cardCreadores.setMaximumSize(new Dimension(Integer.MAX_VALUE, creadoresExpandido ? 240 : 64));
                revalidate();
                repaint();
            }
        };
        cardCreadores.addMouseListener(toggleCreadores);
        headerCreadores.addMouseListener(toggleCreadores);
        chevronCreadores.addMouseListener(toggleCreadores);

        separator = new JPanel();

        separator.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1
                )
        );

        separator.setBackground(
                ThemeManager.borde()
        );

        separator.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        labelVersion = new JLabel(
                "Trullo v1.0  •  Hecho por Argentinos en Argentina"
        );

        labelVersion.setFont(
                new Font("Segoe UI", Font.PLAIN, 12)
        );

        labelVersion.setForeground(
                ThemeManager.textoSuave()
        );

        labelVersion.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(28));
        contenido.add(cardTema);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(cardIdioma);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(cardNotificacion);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(cardTerminos);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(cardCreadores);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(separator);
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(labelVersion);

        scrollPane = new JScrollPane(contenido);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ThemeManager.fondo());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel crearFilaCreador(String nombre, String rol, Color color) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String ini = nombre.substring(0,1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(ini);
                g2.drawString(ini, 16 - w/2, 20);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lNombre = new JLabel(nombre);
        lNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lNombre.setForeground(ThemeManager.texto());

        JLabel lRol = new JLabel(rol);
        lRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lRol.setForeground(ThemeManager.textoSuave());

        textos.add(lNombre);
        textos.add(lRol);

        fila.add(avatar, BorderLayout.WEST);
        fila.add(textos, BorderLayout.CENTER);
        return fila;
    }

    private JLabel crearLabelContacto(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(ThemeManager.textoSuave());
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(ThemeManager.input());
        combo.setForeground(ThemeManager.texto());
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createLineBorder(ThemeManager.borde(), 1, true));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                if (isSelected) {
                    lbl.setBackground(ThemeManager.AZUL);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(ThemeManager.input());
                    lbl.setForeground(ThemeManager.texto());
                }
                return lbl;
            }
        });
    }

    private void actualizarCampoNotificacion() {
        String sel = (String) comboNotificacion.getSelectedItem();
        if (sel == null || sel.equals("No notificar")) {
            panelNotiInputWrapper.setVisible(false);
        } else if (sel.equals("WhatsApp")) {
            labelCampoNoti.setText("WhatsApp");
            campoNotificacion.setText("");
            campoNotificacion.putClientProperty("JTextField.placeholderText", "+54 9 11 1234-5678");
            panelNotiInputWrapper.setVisible(true);
        } else if (sel.equals("Email")) {
            labelCampoNoti.setText("Email");
            campoNotificacion.setText("");
            campoNotificacion.putClientProperty("JTextField.placeholderText", "tu@email.com");
            panelNotiInputWrapper.setVisible(true);
        }
        if (panelNotiInputWrapper.isVisible()) {
            cardNotificacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        } else {
            cardNotificacion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        }
        revalidate();
        repaint();
    }

    private void configurarActualizacionTema() {

        ThemeManager.onThemeChange(() -> {

            setBackground(ThemeManager.fondo());
            if (contenidoRef != null) contenidoRef.setBackground(ThemeManager.fondo());
            if (scrollPane != null) {
                scrollPane.getViewport().setBackground(ThemeManager.fondo());
                scrollPane.getViewport().repaint();
            }
            titulo.setForeground(ThemeManager.texto());
            labelIcono.setForeground(ThemeManager.AZUL);
            labelTexto.setForeground(ThemeManager.texto());
            labelVersion.setForeground(ThemeManager.textoSuave());
            separator.setBackground(ThemeManager.borde());
            chevronTerminos.setForeground(ThemeManager.textoSuave());
            chevronCreadores.setForeground(ThemeManager.textoSuave());

            comboIdioma.setBackground(ThemeManager.input());
            comboIdioma.setForeground(ThemeManager.texto());
            comboIdioma.setBorder(BorderFactory.createLineBorder(ThemeManager.borde(), 1, true));

            comboNotificacion.setBackground(ThemeManager.input());
            comboNotificacion.setForeground(ThemeManager.texto());
            comboNotificacion.setBorder(BorderFactory.createLineBorder(ThemeManager.borde(), 1, true));

            campoNotificacion.setBackground(ThemeManager.input());
            campoNotificacion.setForeground(ThemeManager.texto());
            campoNotificacion.setCaretColor(ThemeManager.texto());
            campoNotificacion.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.borde(), 1, true),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            labelCampoNoti.setForeground(ThemeManager.textoSuave());

            actualizarToggle();

            cardTema.repaint();
            cardIdioma.repaint();
            cardNotificacion.repaint();
            cardTerminos.repaint();
            cardCreadores.repaint();
            repaint();
        });
    }

    private void actualizarToggle() {

        Icon icono;
        if (ThemeManager.isDark()) {
            toggle.setBackground(ThemeManager.AZUL);
            toggle.setToolTipText("Cambiar a modo claro");
            toggle.setSelected(true);
            icono = new Icon() {
                @Override public int getIconWidth() { return 16; }
                @Override public int getIconHeight() { return 16; }
                @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(x, y, 14, 14);
                    g2.setColor(ThemeManager.AZUL);
                    g2.fillOval(x+5, y+1, 10, 10);
                    g2.dispose();
                }
            };
        } else {
            toggle.setBackground(new Color(180, 185, 198));
            toggle.setToolTipText("Cambiar a modo oscuro");
            toggle.setSelected(false);
            icono = new Icon() {
                @Override public int getIconWidth() { return 16; }
                @Override public int getIconHeight() { return 16; }
                @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 220, 80));
                    g2.fillOval(x+2, y+2, 12, 12);
                    g2.setColor(new Color(255, 220, 80));
                    g2.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    for (int i=0;i<4;i++) {
                        double ang = Math.toRadians(i*90);
                        int x1 = (int)(x+8 + Math.cos(ang)*7);
                        int y1 = (int)(y+8 + Math.sin(ang)*7);
                        int x2 = (int)(x+8 + Math.cos(ang)*9);
                        int y2 = (int)(y+8 + Math.sin(ang)*9);
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    g2.dispose();
                }
            };
        }
        toggle.setIcon(icono);
        toggle.setText("");
        toggle.setHorizontalAlignment(SwingConstants.CENTER);
        toggle.setHorizontalTextPosition(SwingConstants.CENTER);
    }
}

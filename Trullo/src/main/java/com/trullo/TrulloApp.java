package com.trullo;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.trullo.ui.*;
import com.trullo.util.EmojiIcon;

import javax.swing.*;
import java.awt.*;

// Esta es la clase principal de la app, aca arranca todo
// basicamente es el punto de entrada del programa
public class TrulloApp {

    // el frame principal de la ventana, la ventana entera basically
    private static JFrame frame;

    // el metodo main, aca empieza la magia
    public static void main(String[] args) {
        // seteamos el tema oscuro por defecto, porque claro es para los débiles
        ThemeManager.setMode(ThemeManager.Mode.DARK);
        aplicarTema();

        // aca registramos que pasa cuando cambia el tema
        //Basicamente le decimos "che, cuando cambies, actualiza todo"
        ThemeManager.onThemeChange(() -> {
            aplicarTema();
            SwingUtilities.updateComponentTreeUI(frame);
            frame.getContentPane().setBackground(ThemeManager.fondo());
            frame.repaint();
        });

        // invocamos el EDT porque Swing es así de caprichoso
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Trullo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1440, 800); // tamaño de la ventana, queda bien en 1080p
            frame.setLocationRelativeTo(null); // que aparezca centrada en pantalla
            frame.getContentPane().setBackground(ThemeManager.fondo());

            // panel principal donde van a ir cambiando las secciones
            JPanel contenido = new JPanel(new BorderLayout());
            contenido.setBackground(ThemeManager.fondo());

            // preparamos todos los paneles de las secciones
            JPanel panelTareas = new PanelTareas();
            JPanel panelCalendario = new PanelCalendario();
            JPanel panelNotas = new PanelNotas();
            JPanel panelConfig = crearPanelConfig();

            // panel de bienvenida que se muestra al iniciar
            JPanel panelBienvenida = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.fondo());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            panelBienvenida.setOpaque(false);

            JPanel cardBienvenida = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.tarjeta());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                    g2.dispose();
                }
            };
            cardBienvenida.setOpaque(false);
            cardBienvenida.setLayout(new BoxLayout(cardBienvenida, BoxLayout.Y_AXIS));
            cardBienvenida.setBorder(BorderFactory.createEmptyBorder(48, 64, 48, 64));

            // icono de bienvenida pintado manualmente (librito)
            JPanel iconoBienvenida = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.AZUL);
                    // libro abierto: dos rectangulos inclinados
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    // pagina izquierda
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(ThemeManager.AZUL);
                    g2.drawLine(cx - 2, cy - 18, cx - 22, cy - 10);
                    g2.drawLine(cx - 22, cy - 10, cx - 22, cy + 18);
                    g2.drawLine(cx - 22, cy + 18, cx - 2, cy + 10);
                    g2.drawLine(cx - 2, cy + 10, cx - 2, cy - 18);
                    // pagina derecha
                    g2.drawLine(cx + 2, cy - 18, cx + 22, cy - 10);
                    g2.drawLine(cx + 22, cy - 10, cx + 22, cy + 18);
                    g2.drawLine(cx + 22, cy + 18, cx + 2, cy + 10);
                    g2.drawLine(cx + 2, cy + 10, cx + 2, cy - 18);
                    // linea central
                    g2.setColor(ThemeManager.textoSuave());
                    g2.drawLine(cx, cy - 16, cx, cy + 8);
                    g2.dispose();
                }
            };
            iconoBienvenida.setOpaque(false);
            iconoBienvenida.setPreferredSize(new Dimension(60, 50));
            iconoBienvenida.setMaximumSize(new Dimension(60, 50));
            iconoBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel tituloBienvenida = new JLabel("Bienvenido a Trullo", SwingConstants.CENTER);
            tituloBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
            tituloBienvenida.setForeground(ThemeManager.texto());
            tituloBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitulo = new JLabel("Tu app de productividad personal", SwingConstants.CENTER);
            subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            subtitulo.setForeground(ThemeManager.textoSuave());
            subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

            cardBienvenida.add(iconoBienvenida);
            cardBienvenida.add(Box.createVerticalStrut(20));
            cardBienvenida.add(tituloBienvenida);
            cardBienvenida.add(Box.createVerticalStrut(10));
            cardBienvenida.add(subtitulo);

            panelBienvenida.add(cardBienvenida);

            // que se actualicen los colores cuando cambie el tema
            ThemeManager.onThemeChange(() -> {
                contenido.setBackground(ThemeManager.fondo());
                panelConfig.setBackground(ThemeManager.fondo());
                tituloBienvenida.setForeground(ThemeManager.texto());
                subtitulo.setForeground(ThemeManager.textoSuave());
                panelBienvenida.repaint();
            });

            // mostramos el panel de bienvenida al iniciar
            contenido.add(panelBienvenida, BorderLayout.CENTER);

            // el menu lateral, le pasamos una funcion que se ejecuta cuando clickean una opcion
            MenuLateral menu = new MenuLateral(seccion -> {
                contenido.removeAll(); // limpiamos todo lo que habia antes
                switch (seccion) {
                    case "My Tasks":
                        contenido.add(panelTareas, BorderLayout.CENTER);
                        break;
                    case "Calendario":
                        contenido.add(panelCalendario, BorderLayout.CENTER);
                        break;
                    case "Notas":
                        contenido.add(panelNotas, BorderLayout.CENTER);
                        break;
                    default:
                        contenido.add(panelConfig, BorderLayout.CENTER);
                        break;
                }
                contenido.revalidate(); // le decimos al layout que recalcule todo
                contenido.repaint(); // y que repinte
            });

            // un espaciador vacio a la derecha para que no quede todo pegado
            JPanel espaciador = new JPanel();
            espaciador.setOpaque(false);
            espaciador.setPreferredSize(new Dimension(ConstantesUI.ANCHO_ESPACIADOR, 1));
            ThemeManager.onThemeChange(() -> espaciador.setOpaque(false));

            // acomodamos todo en el frame: menu a la izq, contenido al centro, espaciador a la der
            frame.add(menu, BorderLayout.WEST);
            frame.add(contenido, BorderLayout.CENTER);
            frame.add(espaciador, BorderLayout.EAST);

            // desactivamos el boton default para que no se active con Enter
            frame.getRootPane().setDefaultButton(null);
            frame.setVisible(true); // y mostramos la ventana, ya está!
        });
    }

    // este metodo aplica el look and feel segun el modo actual
    // basically cambia entre el tema oscuro y el claro de FlatLaf
    private static void aplicarTema() {
        try {
            if (ThemeManager.isDark()) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // configuramos algunas cosas cosmeticas del tema
        UIManager.put("Component.focusWidth", 0); // sin borde de foco feo
        UIManager.put("Button.arc", 14); // botones redondeados
        UIManager.put("Component.arc", 14); // componentes redondeados
        UIManager.put("TextComponent.arc", 14); // inputs tambien redondeados
    }

    // crea el panel de configuracion, que tiene el toggle del tema
    private static JPanel crearPanelConfig() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.fondo());
        panel.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        // titulo de la seccion
        JLabel titulo = new JLabel("Configuración");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // la card que tiene el toggle del tema, pinta un rectangulo redondeado
        JPanel cardTema = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardTema.setOpaque(false);
        cardTema.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        cardTema.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        cardTema.setAlignmentX(Component.LEFT_ALIGNMENT);

        // icono de la luna, pintado manualmente
        JLabel labelIcono = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.AZUL);
                // media luna
                g2.fillOval(2, 2, 16, 16);
                g2.setColor(ThemeManager.tarjeta());
                g2.fillOval(7, 2, 16, 16);
                g2.dispose();
            }
        };
        labelIcono.setPreferredSize(new Dimension(20, 20));
        labelIcono.setForeground(ThemeManager.AZUL);

        // texto que dice "Modo de apariencia"
        JLabel labelTexto = new JLabel("Modo de apariencia");
        labelTexto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelTexto.setForeground(ThemeManager.texto());

        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.add(labelTexto, BorderLayout.CENTER);

        // el toggle para cambiar entre oscuro y claro
        JToggleButton toggle = new JToggleButton();
        toggle.setOpaque(true);
        toggle.setPreferredSize(new Dimension(52, 28));
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.putClientProperty("JButton.arc", 999); // lo redondeamos fully

        actualizarToggle(toggle);

        // cuando clickean el toggle, cambiamos el tema
        toggle.addActionListener(e -> {
            ThemeManager.toggle();
            actualizarToggle(toggle);
        });

        // acomodamos todo en la card
        cardTema.add(labelIcono, BorderLayout.WEST);
        cardTema.add(info, BorderLayout.CENTER);
        cardTema.add(toggle, BorderLayout.EAST);

        // separador visual
        JPanel separator = new JPanel();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setBackground(ThemeManager.borde());
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        // label de la version
        JLabel labelVersion = new JLabel("Trullo v1.0");
        labelVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelVersion.setForeground(ThemeManager.textoSuave());
        labelVersion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // agregamos todo al panel con espaciadores
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(28));
        panel.add(cardTema);
        panel.add(Box.createVerticalStrut(16));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(16));
        panel.add(labelVersion);

        // listener para cuando cambia el tema, actualiza todos los colores
        ThemeManager.onThemeChange(() -> {
            panel.setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            labelIcono.setForeground(ThemeManager.AZUL);
            labelTexto.setForeground(ThemeManager.texto());
            labelVersion.setForeground(ThemeManager.textoSuave());
            separator.setBackground(ThemeManager.borde());
            actualizarToggle(toggle);
            cardTema.repaint();
        });

        return panel;
    }

    // actualiza el aspecto del toggle segun el tema actual
    private static void actualizarToggle(JToggleButton toggle) {
        if (ThemeManager.isDark()) {
            toggle.setBackground(ThemeManager.AZUL);
            toggle.setToolTipText("Cambiar a modo claro");
        } else {
            toggle.setBackground(new Color(180, 185, 198));
            toggle.setToolTipText("Cambiar a modo oscuro");
        }
    }
}

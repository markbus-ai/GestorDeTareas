package com.trullo;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.trullo.frontend.*;
import com.trullo.recursos.EmojiIcon;

import javax.swing.*;
import java.awt.*;

// clase principal, aca arranca todo literal
// si buscas donde empieza el programa es aca
public class TrulloApp {

    // la ventana principal, la guardo aca para poder tocarla despues (cambiar tema etc)
    private static JFrame frame;

    // el main de toda la vida, aca entra java si o si
    public static void main(String[] args) {
        // lo pongo en oscuro de entrada porque el claro te quema los ojos jaja
        ThemeManager.setMode(ThemeManager.Mode.DARK);
        aplicarTema(); // le mando el look and feel

        // cuando alguien cambia el tema (del panel config) que se actualice todo
        // basicamente le digo "che cuando cambie el tema hace esto"
        ThemeManager.onThemeChange(() -> {
            aplicarTema();
            SwingUtilities.updateComponentTreeUI(frame); // esto es para que swing repinte todo con el nuevo tema
            frame.getContentPane().setBackground(ThemeManager.fondo());
            frame.repaint();
        });

        // esto va en el hilo de swing si o si, sino se rompe todo
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Trullo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1440, 800); // tamaño piola para 1080p, ni muy grande ni muy chico
            frame.setLocationRelativeTo(null); // centrado al medio
            frame.getContentPane().setBackground(ThemeManager.fondo());

            // este es el panel del medio donde voy cambiando las pantallas
            JPanel contenido = new JPanel(new BorderLayout());
            contenido.setBackground(ThemeManager.fondo());

            // armo los paneles una sola vez asi no los creo a cada rato
            JPanel panelTareas = new PanelTareas();
            JPanel panelCalendario = new PanelCalendario();
            JPanel panelNotas = new PanelNotas();
            JPanel panelConfig = crearPanelConfig(); // este lo armo aca mismo

            // pantalla de bienvenida que se ve al abrir la app
            JPanel panelBienvenida = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    // pinto el fondo a mano para que quede liso con el tema
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.fondo());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                }
            };
            panelBienvenida.setOpaque(false);

            // la tarjetita blanca/oscura del medio
            JPanel cardBienvenida = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.tarjeta());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24); // redondeado piola
                    g2.dispose();
                }
            };
            cardBienvenida.setOpaque(false);
            cardBienvenida.setLayout(new BoxLayout(cardBienvenida, BoxLayout.Y_AXIS));
            cardBienvenida.setBorder(BorderFactory.createEmptyBorder(48, 64, 48, 64));

            // iconito del librito hecho a mano con lineas, no es imagen
            JPanel iconoBienvenida = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(ThemeManager.AZUL);
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    // dibujo las dos tapas del libro
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(ThemeManager.AZUL);
                    // lado izq
                    g2.drawLine(cx - 2, cy - 18, cx - 22, cy - 10);
                    g2.drawLine(cx - 22, cy - 10, cx - 22, cy + 18);
                    g2.drawLine(cx - 22, cy + 18, cx - 2, cy + 10);
                    g2.drawLine(cx - 2, cy + 10, cx - 2, cy - 18);
                    // lado der
                    g2.drawLine(cx + 2, cy - 18, cx + 22, cy - 10);
                    g2.drawLine(cx + 22, cy - 10, cx + 22, cy + 18);
                    g2.drawLine(cx + 22, cy + 18, cx + 2, cy + 10);
                    g2.drawLine(cx + 2, cy + 10, cx + 2, cy - 18);
                    // lineita del medio
                    g2.setColor(ThemeManager.textoSuave());
                    g2.drawLine(cx, cy - 16, cx, cy + 8);
                    g2.dispose();
                }
            };
            iconoBienvenida.setOpaque(false);
            iconoBienvenida.setPreferredSize(new Dimension(60, 50));
            iconoBienvenida.setMaximumSize(new Dimension(60, 50));
            iconoBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

            // textos de bienvenida
            JLabel tituloBienvenida = new JLabel("Bienvenido a Trullo", SwingConstants.CENTER);
            tituloBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 28));
            tituloBienvenida.setForeground(ThemeManager.texto());
            tituloBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel subtitulo = new JLabel("Tu app de productividad personal", SwingConstants.CENTER);
            subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            subtitulo.setForeground(ThemeManager.textoSuave());
            subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

            // meto todo en la card
            cardBienvenida.add(iconoBienvenida);
            cardBienvenida.add(Box.createVerticalStrut(20));
            cardBienvenida.add(tituloBienvenida);
            cardBienvenida.add(Box.createVerticalStrut(10));
            cardBienvenida.add(subtitulo);

            panelBienvenida.add(cardBienvenida);

            // si cambia el tema que se repinte la bienvenida tambien
            ThemeManager.onThemeChange(() -> {
                contenido.setBackground(ThemeManager.fondo());
                panelConfig.setBackground(ThemeManager.fondo());
                tituloBienvenida.setForeground(ThemeManager.texto());
                subtitulo.setForeground(ThemeManager.textoSuave());
                panelBienvenida.repaint();
            });

            // al inicio muestro la bienvenida
            contenido.add(panelBienvenida, BorderLayout.CENTER);

            // menu de la izquierda, le paso que hacer cuando tocan algo
            MenuLateral menu = new MenuLateral(seccion -> {
                contenido.removeAll(); // saco lo que habia
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
                contenido.revalidate(); // recalculo el layout
                contenido.repaint();
            });

            // espaciador a la derecha para que no quede todo pegado al borde, es visual nomas
            JPanel espaciador = new JPanel();
            espaciador.setOpaque(false);
            espaciador.setPreferredSize(new Dimension(ConstantesUI.ANCHO_ESPACIADOR, 1));
            ThemeManager.onThemeChange(() -> espaciador.setOpaque(false));

            // armo el frame final
            frame.add(menu, BorderLayout.WEST);
            frame.add(contenido, BorderLayout.CENTER);
            frame.add(espaciador, BorderLayout.EAST);

            frame.getRootPane().setDefaultButton(null); // saco el enter por defecto que molesta
            frame.setVisible(true); // y ahi si, muestro todo
        });
    }

    // cambia el look and feel segun si es dark o light
    // flatlaf es el que hace que se vea moderno
    private static void aplicarTema() {
        try {
            if (ThemeManager.isDark()) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception e) {
            e.printStackTrace(); // si explota que lo escupa por consola y listo
        }
        // detalles para que quede mas lindo
        UIManager.put("Component.focusWidth", 0); // le saco el borde feo de focus
        UIManager.put("Button.arc", 14); // botones redondeados
        UIManager.put("Component.arc", 14);
        UIManager.put("TextComponent.arc", 14);
    }

    // panel de config, por ahora solo tiene el switch de tema pero despues le agrego mas
    private static JPanel crearPanelConfig() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.fondo());
        panel.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        JLabel titulo = new JLabel("Configuración");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // card del tema, la pinto redondeada a mano
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

        // lunita dibujada a mano, dos circulos y queda la luna
        JLabel labelIcono = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.AZUL);
                g2.fillOval(2, 2, 16, 16);
                g2.setColor(ThemeManager.tarjeta());
                g2.fillOval(7, 2, 16, 16);
                g2.dispose();
            }
        };
        labelIcono.setPreferredSize(new Dimension(20, 20));
        labelIcono.setForeground(ThemeManager.AZUL);

        JLabel labelTexto = new JLabel("Modo de apariencia");
        labelTexto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelTexto.setForeground(ThemeManager.texto());

        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.add(labelTexto, BorderLayout.CENTER);

        // el toggle piola para cambiar de tema
        JToggleButton toggle = new JToggleButton();
        toggle.setOpaque(true);
        toggle.setPreferredSize(new Dimension(52, 28));
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.putClientProperty("JButton.arc", 999); // bien redondo

        actualizarToggle(toggle);

        // cuando lo tocan cambia el tema
        toggle.addActionListener(e -> {
            ThemeManager.toggle();
            actualizarToggle(toggle);
        });

        cardTema.add(labelIcono, BorderLayout.WEST);
        cardTema.add(info, BorderLayout.CENTER);
        cardTema.add(toggle, BorderLayout.EAST);

        // lineita separadora
        JPanel separator = new JPanel();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setBackground(ThemeManager.borde());
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel labelVersion = new JLabel("Trullo v1.0");
        labelVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelVersion.setForeground(ThemeManager.textoSuave());
        labelVersion.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(28));
        panel.add(cardTema);
        panel.add(Box.createVerticalStrut(16));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(16));
        panel.add(labelVersion);

        // que se actualice si cambia el tema
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

    // le cambia el color al toggle segun el tema, nada mas
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

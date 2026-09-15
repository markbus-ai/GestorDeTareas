package com.trullo;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.trullo.frontend.*;
import com.trullo.recursos.EmojiIcon;
import javax.swing.*;
import java.awt.*;

// Clase principal. Acá arranca toda la aplicación.
public class TrulloApp {

    // Ventana principal para poder actualizarla cuando cambia el tema.
    private static JFrame frame;

    // Punto de entrada de la aplicación.
    public static void main(String[] args) {

        // Arranca en modo oscuro.
        ThemeManager.setMode(ThemeManager.Mode.DARK);
        aplicarTema();

        // Cuando cambia el tema, actualizamos toda la interfaz.
        ThemeManager.onThemeChange(() -> {
            aplicarTema();

            if (frame != null) {
                SwingUtilities.updateComponentTreeUI(frame);
                frame.getContentPane().setBackground(ThemeManager.fondo());
                frame.repaint();
            }
        });

        // Todo lo relacionado con Swing se ejecuta en el hilo de eventos.
        SwingUtilities.invokeLater(() -> {

            frame = new JFrame("Trullo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1440, 800);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(ThemeManager.fondo());

            // Contenedor central donde se van cambiando las pantallas.
            JPanel contenido = new JPanel(new BorderLayout());
            contenido.setBackground(ThemeManager.fondo());

            // Creamos las pantallas una sola vez.
            JPanel panelTareas = new PanelTareas();
            JPanel panelCalendario = new PanelCalendario();
            JPanel panelNotas = new PanelNotas();
            JPanel panelConfig = new PanelConfiguracion();

            // Pantalla de bienvenida.
            JPanel panelBienvenida = new JPanel(new GridBagLayout()) {

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setColor(ThemeManager.fondo());
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    g2.dispose();
                }
            };

            panelBienvenida.setOpaque(false);

            // Tarjeta central de bienvenida.
            JPanel cardBienvenida = new JPanel() {

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setColor(ThemeManager.tarjeta());
                    g2.fillRoundRect(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            24,
                            24
                    );

                    g2.dispose();
                }
            };

            cardBienvenida.setOpaque(false);
            cardBienvenida.setLayout(
                    new BoxLayout(cardBienvenida, BoxLayout.Y_AXIS)
            );

            cardBienvenida.setBorder(
                    BorderFactory.createEmptyBorder(48, 64, 48, 64)
            );

            // Icono de bienvenida - igual a My Tasks (memo -> \uD83D\uDCDD)
            JLabel iconoBienvenida = new JLabel(new EmojiIcon("\uD83D\uDCDD", 42));
            iconoBienvenida.setOpaque(false);
            iconoBienvenida.setPreferredSize(new Dimension(70, 60));
            iconoBienvenida.setMaximumSize(new Dimension(70, 60));
            iconoBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
            iconoBienvenida.setHorizontalAlignment(SwingConstants.CENTER);

            // Título.
            JLabel tituloBienvenida = new JLabel(
                    "Bienvenido a Trullo",
                    SwingConstants.CENTER
            );

            tituloBienvenida.setFont(
                    new Font("Segoe UI", Font.BOLD, 28)
            );

            tituloBienvenida.setForeground(ThemeManager.texto());
            tituloBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Subtítulo.
            JLabel subtitulo = new JLabel(
                    "Tu app de productividad personal",
                    SwingConstants.CENTER
            );

            subtitulo.setFont(
                    new Font("Segoe UI", Font.PLAIN, 15)
            );

            subtitulo.setForeground(ThemeManager.textoSuave());
            subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Agregamos los componentes a la tarjeta.
            cardBienvenida.add(iconoBienvenida);
            cardBienvenida.add(Box.createVerticalStrut(20));
            cardBienvenida.add(tituloBienvenida);
            cardBienvenida.add(Box.createVerticalStrut(10));
            cardBienvenida.add(subtitulo);

            panelBienvenida.add(cardBienvenida);

            // Actualización visual cuando cambia el tema.
            ThemeManager.onThemeChange(() -> {
                contenido.setBackground(ThemeManager.fondo());
                tituloBienvenida.setForeground(ThemeManager.texto());
                subtitulo.setForeground(ThemeManager.textoSuave());
                panelBienvenida.repaint();
            });

            // Pantalla inicial.
            contenido.add(
                    panelBienvenida,
                    BorderLayout.CENTER
            );

            // Menú lateral.
            MenuLateral menu = new MenuLateral(seccion -> {

                contenido.removeAll();

                switch (seccion) {

                    case "My Tasks":
                        contenido.add(
                                panelTareas,
                                BorderLayout.CENTER
                        );
                        break;

                    case "Calendario":
                        contenido.add(
                                panelCalendario,
                                BorderLayout.CENTER
                        );
                        break;

                    case "Notas":
                        contenido.add(
                                panelNotas,
                                BorderLayout.CENTER
                        );
                        break;

                    default:
                        contenido.add(
                                panelConfig,
                                BorderLayout.CENTER
                        );
                        break;
                }

                contenido.revalidate();
                contenido.repaint();
            });

            // Espaciador derecho.
            JPanel espaciador = new JPanel();
            espaciador.setOpaque(false);

            espaciador.setPreferredSize(
                    new Dimension(
                            ConstantesUI.ANCHO_ESPACIADOR,
                            1
                    )
            );

            // Construimos la ventana.
            frame.add(menu, BorderLayout.WEST);
            frame.add(contenido, BorderLayout.CENTER);
            frame.add(espaciador, BorderLayout.EAST);

            frame.getRootPane().setDefaultButton(null);

            frame.setVisible(true);
        });
    }

    // Aplica el tema actual.
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

        // Detalles visuales.
        UIManager.put("Component.focusWidth", 0);
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 14);
        UIManager.put("TextComponent.arc", 14);
    }
}

package com.trullo.frontend;

import com.trullo.recursos.EmojiIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

// sidebar izquierda con los botones para moverse entre pantallas
public class MenuLateral extends JPanel {

    // cuál está marcado ahora
    private JButton botonActivo;

    // la avisa TrulloApp cuando tocan un botón, así cambia la pantalla del medio
    private final Consumer<String> onNavigate;

    public MenuLateral(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(ConstantesUI.ANCHO_MENU, 800));
        setBackground(ThemeManager.menu());

        // contenedor vertical con todo adentro
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(28, 18, 20, 18));

        // logo arriba de todo
        JLabel logo = new JLabel("Trullo");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setName("logo");

        // donde van los 3 botones principales
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new BoxLayout(panelNavegacion, BoxLayout.Y_AXIS));
        panelNavegacion.setOpaque(false);

        JButton btnTasks = crearBotonMenu("\uD83D\uDCDD", "My Tasks", 14);
        JButton btnCalendario = crearBotonMenu("\uD83D\uDCC5", "Calendario", 14);
        JButton btnNotas = crearBotonMenu("\uD83D\uDCDA", "Notas", 14);

        // cada uno se marca y avisa a dónde ir
        btnTasks.addActionListener(e -> { activar(btnTasks); onNavigate.accept("My Tasks"); });
        btnCalendario.addActionListener(e -> { activar(btnCalendario); onNavigate.accept("Calendario"); });
        btnNotas.addActionListener(e -> { activar(btnNotas); onNavigate.accept("Notas"); });

        panelNavegacion.add(btnTasks);
        panelNavegacion.add(Box.createVerticalStrut(4));
        panelNavegacion.add(btnCalendario);
        panelNavegacion.add(Box.createVerticalStrut(4));
        panelNavegacion.add(btnNotas);

        // parte de abajo con la lineita y config
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setOpaque(false);

        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setName("separador");

        JButton btnConfig = crearBotonMenu("\u2699\uFE0F", "Configuración", 14);
        btnConfig.addActionListener(e -> { activar(btnConfig); onNavigate.accept("Configuración"); });

        panelInferior.add(Box.createVerticalStrut(20));
        panelInferior.add(separador);
        panelInferior.add(Box.createVerticalStrut(12));
        panelInferior.add(btnConfig);

        contenido.add(logo);
        contenido.add(panelNavegacion);
        // este glue empuja lo de abajo bien al fondo
        contenido.add(Box.createVerticalGlue());
        contenido.add(panelInferior);

        add(contenido, BorderLayout.CENTER);

        // si cambia el tema actualizo colores y reaplico el activo
        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.menu());
            logo.setForeground(ThemeManager.texto());
            separador.setBackground(ThemeManager.borde());
            activar(botonActivo);
            revalidate();
            repaint();
        });

        // arranca en tareas seleccionado
        activar(btnTasks);
    }

    // marca uno como activo y desmarca el anterior
    private void activar(JButton boton) {
        if (botonActivo != null) {
            botonActivo.setBackground(null); // le saco el fondo
            botonActivo.setForeground(ThemeManager.textoSuave());
        }
        botonActivo = boton;
        if (boton != null) {
            boton.setBackground(ThemeManager.menuActivo());
            boton.setForeground(ThemeManager.AZUL);
        }
    }

    // fábrica de botones para no repetir el estilo en cada uno
    private JButton crearBotonMenu(String icono, String texto, int tamanoFont) {
        JButton boton = new JButton(texto);
        // iconito un toque más grande que el texto
        boton.setIcon(new EmojiIcon(icono, tamanoFont + 6));

        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(12);
        boton.setHorizontalTextPosition(SwingConstants.RIGHT);
        boton.setVerticalTextPosition(SwingConstants.CENTER);

        boton.setFont(new Font("Segoe UI", Font.PLAIN, tamanoFont));
        boton.setForeground(ThemeManager.textoSuave());
        boton.setBackground(null);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        boton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        boton.setFocusPainted(false);
        boton.setOpaque(true);

        // redondeadito con flatlaf
        boton.putClientProperty("JButton.buttonType", "roundRect");
        boton.putClientProperty("JButton.arc", 12);

        // hover piola: si no es el activo se ilumina un toque al pasar el mouse
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (boton != botonActivo) {
                    boton.setBackground(ThemeManager.menuHover());
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (boton != botonActivo) {
                    boton.setBackground(null);
                }
            }
        });

        ThemeManager.onThemeChange(() -> {
            if (boton != botonActivo) {
                boton.setForeground(ThemeManager.textoSuave());
                boton.setBackground(null);
            }
        });

        return boton;
    }
}

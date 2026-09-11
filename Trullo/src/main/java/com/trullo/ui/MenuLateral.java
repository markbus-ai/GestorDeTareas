package com.trullo.ui;

import com.trullo.util.EmojiIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

// Esta clase es el menu lateral de la app
// tiene los botones de navegacion para ir a cada seccion
// y maneja el estado de que boton esta activo
public class MenuLateral extends JPanel {

    // el boton que esta seleccionado actualmente
    private JButton botonActivo;

    // callback que se ejecuta cuando clickean una opcion del menu
    // le pasa el nombre de la seccion al listener principal
    private final Consumer<String> onNavigate;

    // constructor, recibe la funcion que se ejecuta al navegar
    public MenuLateral(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(ConstantesUI.ANCHO_MENU, 800));
        setBackground(ThemeManager.menu());

        // panel donde van todos los elementos del menu
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(28, 18, 20, 18));

        // el logo de la app arriba del todo
        JLabel logo = new JLabel("Trullo");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setName("logo");

        // panel de navegacion donde van los botones principales
        JPanel panelNavegacion = new JPanel();
        panelNavegacion.setLayout(new BoxLayout(panelNavegacion, BoxLayout.Y_AXIS));
        panelNavegacion.setOpaque(false);

        // creamos los botones del menu con sus emojis
        JButton btnTasks = crearBotonMenu("\uD83D\uDCDD", "My Tasks", 14);
        JButton btnCalendario = crearBotonMenu("\uD83D\uDCC5", "Calendario", 14);
        JButton btnNotas = crearBotonMenu("\uD83D\uDCDA", "Notas", 14);

        // les agregamos las acciones a cada boton
        // cuando clickean, activan el boton y avisan a navegacion
        btnTasks.addActionListener(e -> { activar(btnTasks); onNavigate.accept("My Tasks"); });
        btnCalendario.addActionListener(e -> { activar(btnCalendario); onNavigate.accept("Calendario"); });
        btnNotas.addActionListener(e -> { activar(btnNotas); onNavigate.accept("Notas"); });

        // agregamos los botones al panel con espaciadores entre ellos
        panelNavegacion.add(btnTasks);
        panelNavegacion.add(Box.createVerticalStrut(4));
        panelNavegacion.add(btnCalendario);
        panelNavegacion.add(Box.createVerticalStrut(4));
        panelNavegacion.add(btnNotas);

        // panel inferior que tiene el separador y el boton de configuracion
        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setOpaque(false);

        // separador visual entre las opciones principales y config
        JSeparator separador = new JSeparator();
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setName("separador");

        // boton de configuracion abajo del todo
        JButton btnConfig = crearBotonMenu("\u2699\uFE0F", "Configuración", 14);
        btnConfig.addActionListener(e -> { activar(btnConfig); onNavigate.accept("Configuración"); });

        // acomodamos el panel inferior
        panelInferior.add(Box.createVerticalStrut(20));
        panelInferior.add(separador);
        panelInferior.add(Box.createVerticalStrut(12));
        panelInferior.add(btnConfig);

        // agregamos todo al contenido: logo arriba, navegacion, y abajo config
        contenido.add(logo);
        contenido.add(panelNavegacion);
        contenido.add(Box.createVerticalGlue()); // espacio flexible que empuja config para abajo
        contenido.add(panelInferior);

        add(contenido, BorderLayout.CENTER);

        // listener para cambiar colores cuando cambia el tema
        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.menu());
            logo.setForeground(ThemeManager.texto());
            separador.setBackground(ThemeManager.borde());
            activar(botonActivo); // reactivamos el boton actual para actualizar colores
            revalidate();
            repaint();
        });

        activar(btnTasks); // activamos "My Tasks" por defecto
    }

    // activa un boton y desactiva el anterior
    // es como un radio button pero con botones custom
    private void activar(JButton boton) {
        if (botonActivo != null) {
            botonActivo.setBackground(null); // le sacamos el fondo al boton que estaba activo
            botonActivo.setForeground(ThemeManager.textoSuave());
        }
        botonActivo = boton;
        if (boton != null) {
            boton.setBackground(ThemeManager.menuActivo()); // le ponemos el fondo azul suave
            boton.setForeground(ThemeManager.AZUL); // y el texto azul
        }
    }

    // crea un boton del menu con su emoji y texto
    // recibe el emoji (como string unicode), el texto y el tamaño de la fuente
    private JButton crearBotonMenu(String icono, String texto, int tamanoFont) {
        JButton boton = new JButton(texto);
        boton.setIcon(new EmojiIcon(icono, tamanoFont + 6)); // el icono un poco mas grande que el texto

        // configuramos la alineacion del boton
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setIconTextGap(12); // espacio entre icono y texto
        boton.setHorizontalTextPosition(SwingConstants.RIGHT);
        boton.setVerticalTextPosition(SwingConstants.CENTER);

        // estilos del boton
        boton.setFont(new Font("Segoe UI", Font.PLAIN, tamanoFont));
        boton.setForeground(ThemeManager.textoSuave()); // color gris por defecto
        boton.setBackground(null); // sin fondo por defecto
        boton.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 10));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44)); // alto fijo
        boton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        boton.setFocusPainted(false); // sin borde de foco
        boton.setOpaque(true); // para que se vea el background

        // propiedades de FlatLaf para que sea redondeado
        boton.putClientProperty("JButton.buttonType", "roundRect");
        boton.putClientProperty("JButton.arc", 12);

        // efecto de hover: cuando pasas el mouse por encima, cambia el fondo
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
                    boton.setBackground(null); // volvemos al fondo transparente
                }
            }
        });

        // listener para cuando cambia el tema
        ThemeManager.onThemeChange(() -> {
            if (boton != botonActivo) {
                boton.setForeground(ThemeManager.textoSuave());
                boton.setBackground(null);
            }
        });

        return boton;
    }
}

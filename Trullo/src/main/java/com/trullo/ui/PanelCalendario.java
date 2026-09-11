package com.trullo.ui;

import javax.swing.*;
import java.awt.*;

// Panel principal del calendario
// basicamente es un contenedor que junta el calendario mensual y el panel de recordatorios
// es como un layout wrapper que organiza las dos partes
public class PanelCalendario extends JPanel {

    // constructor, arma el panel con calendario a la izq y recordatorios a la der
    public PanelCalendario() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));
        setBackground(ThemeManager.fondo());

        // titulo de la seccion
        JLabel titulo = new JLabel("Calendario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        // el calendario mensual que muestra los dias
        CalendarioMensual calendario = new CalendarioMensual();

        // el panel de recordatorios que va al lado del calendario
        PanelRecordatorios panelRecordatorios = new PanelRecordatorios(calendario);

        // fila central que tiene el calendario a la izquierda y recordatorios al centro/derecha
        JPanel filaCentral = new JPanel(new BorderLayout());
        filaCentral.setOpaque(false);
        filaCentral.add(calendario, BorderLayout.WEST);    // calendario a la izquierda
        filaCentral.add(panelRecordatorios, BorderLayout.CENTER); // recordatorios al centro

        // acomodamos todo
        add(titulo, BorderLayout.NORTH);
        add(filaCentral, BorderLayout.CENTER);

        // listener para cambio de tema
        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            revalidate();
            repaint();
        });
    }
}

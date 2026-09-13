package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;

public class PanelCalendario extends JPanel {

    public PanelCalendario() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));
        setBackground(ThemeManager.fondo());

        JLabel titulo = new JLabel("Calendario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        CalendarioMensual calendario = new CalendarioMensual();
        PanelRecordatorios panelRecordatorios = new PanelRecordatorios(calendario);

        JPanel filaCentral = new JPanel(new BorderLayout());
        filaCentral.setOpaque(false);
        filaCentral.add(calendario, BorderLayout.WEST);
        filaCentral.add(panelRecordatorios, BorderLayout.CENTER);

        add(titulo, BorderLayout.NORTH);
        add(filaCentral, BorderLayout.CENTER);

        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            revalidate();
            repaint();
        });
    }
}

package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;

// este panel es solo un wrapper que junta el calendario y los recordatorios lado a lado
// no hace mucho mas, es para ordenar el layout nomas
public class PanelCalendario extends JPanel {

    public PanelCalendario() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));
        setBackground(ThemeManager.fondo());

        // titulito arriba
        JLabel titulo = new JLabel("Calendario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        // los dos paneles posta
        CalendarioMensual calendario = new CalendarioMensual();
        PanelRecordatorios panelRecordatorios = new PanelRecordatorios(calendario); // le paso el calendario para que se hablen

        // los pongo uno al lado del otro
        JPanel filaCentral = new JPanel(new BorderLayout());
        filaCentral.setOpaque(false);
        filaCentral.add(calendario, BorderLayout.WEST);
        filaCentral.add(panelRecordatorios, BorderLayout.CENTER);

        add(titulo, BorderLayout.NORTH);
        add(filaCentral, BorderLayout.CENTER);

        // si cambia el tema repinto
        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            revalidate();
            repaint();
        });
    }
}

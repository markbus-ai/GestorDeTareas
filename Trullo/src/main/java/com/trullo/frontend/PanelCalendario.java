package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;

// wrapper que junta el calendario y los recordatorios lado a lado, no hace mucho más
public class PanelCalendario extends JPanel {

    public PanelCalendario() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));
        setBackground(ThemeManager.fondo());

        // titulito de arriba
        JLabel titulo = new JLabel("Calendario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.texto());
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 22, 0));

        CalendarioMensual calendario = new CalendarioMensual();
        // le paso el calendario para que se hablen entre ellos
        PanelRecordatorios panelRecordatorios = new PanelRecordatorios(calendario);

        // cada uno en su card para que se vean separaditos
        JPanel cardCalendario = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardCalendario.setOpaque(false);
        cardCalendario.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        cardCalendario.add(calendario, BorderLayout.CENTER);

        JPanel cardRecordatorios = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                ConstantesUI.paintRoundRect(g, getWidth(), getHeight(), ThemeManager.tarjeta(), ConstantesUI.RADIO);
            }
        };
        cardRecordatorios.setOpaque(false);
        cardRecordatorios.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        cardRecordatorios.add(panelRecordatorios, BorderLayout.CENTER);

        // uno al lado del otro, calendario fijo a la izquierda y recordatorios estirándose
        JPanel filaCentral = new JPanel(new BorderLayout(24, 0));
        filaCentral.setOpaque(false);
        filaCentral.add(cardCalendario, BorderLayout.WEST);
        filaCentral.add(cardRecordatorios, BorderLayout.CENTER);

        add(titulo, BorderLayout.NORTH);
        add(filaCentral, BorderLayout.CENTER);

        // si cambia el tema repinto todo
        ThemeManager.onThemeChange(() -> {
            setBackground(ThemeManager.fondo());
            titulo.setForeground(ThemeManager.texto());
            cardCalendario.repaint();
            cardRecordatorios.repaint();
            revalidate();
            repaint();
        });
    }
}

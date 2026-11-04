package com.trullo.frontend;

import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Chip redondeado que se usa como filtro en la pantalla de tareas (Todas / Pendientes /
 * Completadas).
 */
public class FilterChip extends JToggleButton {

    /**
     * Crea el chip con su texto y lo deja escuchando los cambios de tema.
     */
    public FilterChip(String text) {
        super(text);
        applyChipStyle();
        Theme.addListener(() -> {
            applyChipStyle();
            repaint();
        });
    }

    /**
     * Aplica tipografía, cursor, padding y color de texto según el estado.
     */
    private void applyChipStyle() {
        setFont(UiKit.font(Font.BOLD, 12));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(7, 16, 7, 16));
        updateForeground();
    }

    /**
     * Al seleccionar/deseleccionar, actualiza el color del texto y repinta.
     */
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        updateForeground();
        repaint();
    }

    /**
     * Blanco sobre el fondo azul cuando está activo; gris cuando no.
     */
    private void updateForeground() {
        setForeground(isSelected() ? Color.WHITE : Theme.mutedText());
    }

    /**
     * Pinta la píldora de fondo (azul si está activo) y después dibuja el texto encima.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (isSelected()) {
            graphics2D.setColor(Theme.accent());
        } else if (getModel().isRollover()) {
            graphics2D.setColor(Theme.subtleBackground());
        } else {
            graphics2D.setColor(Theme.subtleBackground());
        }

        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        graphics2D.dispose();

        setForeground(isSelected() ? Color.WHITE : Theme.mutedText());
        super.paintComponent(graphics);
    }
}

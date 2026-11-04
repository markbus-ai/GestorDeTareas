package com.trullo.frontend;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Etiqueta chica y redondeada para mostrar fechas límite y prioridades de una tarea.
 */
public class TagLabel extends JLabel {

    // Colores fijos que recibe la etiqueta (no dependen del tema).
    private final Color backgroundColor;
    private final Color foregroundColor;

    /**
     * Crea la etiqueta con su texto, fondo y color de letra.
     */
    public TagLabel(String text, Color backgroundColor, Color foregroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        setOpaque(false);
        setFont(UiKit.font(java.awt.Font.BOLD, 11));
        setForeground(foregroundColor);
        setBorder(new EmptyBorder(4, 10, 4, 10));
    }

    /**
     * Pinta la píldora de fondo y dibuja el texto encima.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.setColor(backgroundColor);
        graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        graphics2D.dispose();
        super.paintComponent(graphics);
    }
}

package com.trullo.frontend;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Botón circular flotante (FAB) que usan las pantallas de tareas y notas para crear un ítem.
 */
public class FloatingButton extends JButton {

    // Color de fondo consultado en cada repintado y diámetro del círculo.
    private final Supplier<Color> backgroundSupplier;
    private final int diameter;

    /**
     * Recibe el color, el ícono, el tamaño y el tooltip del botón.
     */
    public FloatingButton(Supplier<Color> backgroundSupplier,
                          AppIcon.Glyph glyph,
                          int diameter,
                          String tooltip) {
        this.backgroundSupplier = Objects.requireNonNull(backgroundSupplier, "backgroundSupplier");
        this.diameter = diameter;

        setIcon(new AppIcon(glyph, 22, Color.WHITE));
        setToolTipText(tooltip);
        applyButtonStyle();
        Theme.addListener(this::applyButtonStyle);
    }

    /**
     * Quita el fondo/borde nativos y fija el tamaño circular del botón.
     */
    private void applyButtonStyle() {
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setMaximumSize(new Dimension(diameter, diameter));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
    }

    /**
     * Dibuja una sombra sutil y el círculo de color; se aclara al pasar el mouse.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        Color backgroundColor = backgroundSupplier.get();
        if (getModel().isRollover()) {
            backgroundColor = backgroundColor.brighter();
        }

        graphics2D.setColor(new Color(0, 0, 0, 36));
        graphics2D.fillOval(2, 3, diameter, diameter);

        graphics2D.setColor(backgroundColor);
        graphics2D.fillOval(0, 0, diameter, diameter);
        graphics2D.dispose();

        super.paintComponent(graphics);
    }
}

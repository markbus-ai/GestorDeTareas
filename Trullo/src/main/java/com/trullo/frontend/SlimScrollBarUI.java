package com.trullo.frontend;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Barra de scroll fina y redondeada que sigue el tema activo y esconde las flechas por defecto.
 */
public class SlimScrollBarUI extends BasicScrollBarUI {

    /**
     * Botón de "subir": se reemplaza por uno invisible.
     */
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createInvisibleButton();
    }

    /**
     * Botón de "bajar": también invisible.
     */
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createInvisibleButton();
    }

    /**
     * Crea un botón de tamaño cero para que no ocupe lugar en la barra.
     */
    private JButton createInvisibleButton() {
        JButton button = new JButton();
        Dimension zeroSize = new Dimension(0, 0);
        button.setPreferredSize(zeroSize);
        button.setMinimumSize(zeroSize);
        button.setMaximumSize(zeroSize);
        return button;
    }

    /**
     * Deja el carril transparente para que se vea la superficie de abajo.
     */
    @Override
    protected void paintTrack(Graphics graphics, JComponent component, Rectangle trackBounds) {
        // Deja el carril transparente para que se siga viendo la superficie de abajo.
    }

    /**
     * Pinta el "thumb" redondeado con el color de scroll del tema.
     */
    @Override
    protected void paintThumb(Graphics graphics, JComponent component, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        graphics2D.setColor(Theme.scrollThumb());
        graphics2D.fillRoundRect(
                thumbBounds.x + 2,
                thumbBounds.y,
                Math.max(4, thumbBounds.width - 4),
                thumbBounds.height,
                8,
                8
        );
        graphics2D.dispose();
    }
}

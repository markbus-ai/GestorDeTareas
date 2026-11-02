package com.trullo.frontend;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Panel que pinta una superficie redondeada. Los colores se piden de forma diferida,
 * así que alcanza con repintar para reflejar un cambio de tema.
 */
public class RoundedPanel extends JPanel {

    // Colores que se consultan en cada repintado, no se guardan fijos.
    private final Supplier<Color> fillSupplier;
    private final Supplier<Color> borderSupplier;
    private final int radius;

    /**
     * Constructor simple: solo color de relleno, sin borde.
     */
    public RoundedPanel(LayoutManager layout, Supplier<Color> fillSupplier, int radius) {
        this(layout, fillSupplier, null, radius);
    }

    /**
     * Constructor completo: relleno, borde opcional y radio de las esquinas.
     */
    public RoundedPanel(LayoutManager layout,
                        Supplier<Color> fillSupplier,
                        Supplier<Color> borderSupplier,
                        int radius) {
        super(layout);
        this.fillSupplier = Objects.requireNonNull(fillSupplier, "fillSupplier");
        this.borderSupplier = borderSupplier;
        this.radius = radius;
        setOpaque(false);
    }

    /**
     * Dibuja el rectángulo redondeado de fondo y, si hay, su borde.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        graphics2D.setColor(fillSupplier.get());
        graphics2D.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);

        if (borderSupplier != null) {
            graphics2D.setColor(borderSupplier.get());
            graphics2D.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);
        }

        graphics2D.dispose();
    }
}

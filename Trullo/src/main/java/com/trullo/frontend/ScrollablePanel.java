package com.trullo.frontend;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 * Panel que siempre acompaña el ancho de su viewport de scroll. Así la grilla de notas y las
 * colecciones de tarjetas se adaptan cuando se redimensiona la ventana.
 */
public final class ScrollablePanel extends JPanel implements Scrollable {

    /**
     * Recibe la distribución de los componentes y queda transparente.
     */
    public ScrollablePanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    /**
     * El tamaño preferido dentro del scroll es el mismo que pide el panel.
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Cuánto baja la rueda del mouse en cada paso.
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
                                          int orientation,
                                          int direction) {
        return 16;
    }

    /**
     * Cuánto baja al hacer click en la barra o Page Down: casi una pantalla completa.
     */
    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
                                           int orientation,
                                           int direction) {
        return Math.max(16, visibleRect.height - 16);
    }

    /**
     * true = el panel se estira para ocupar todo el ancho visible.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * false = el panel puede ser más alto que el viewport y por eso aparece el scroll vertical.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}

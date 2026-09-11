package com.trullo.ui;

import javax.swing.*;
import java.awt.*;

// Esta clase personaliza el scrollbar para que se vea moderno
// en vez del scrollbar gris feo de Java, dibujamos uno custom
// con colores del tema y bordes redondeados
public class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

    // configura los colores del scrollbar
    // el track (fondo) se pone del color del fondo de la app
    @Override
    protected void configureScrollBarColors() {
        trackColor = ThemeManager.fondo();
    }

    // creamos botones de flecha de tamaño cero (los ocultamos)
    // porque no necesitamos las flechitas de subir/bajar
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    // crea un boton invisible de 0x0 pixeles
    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    // dibuja el thumb (la bolita que arrastras para scrollear)
    // le ponemos color del tema y bordes redondeados
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.scrollThumb());
        // dibujamos un rectangulo redondeado, un poco mas angosto que el track
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y,
                thumbBounds.width - 4, thumbBounds.height, 8, 8);
        g2.dispose();
    }

    // dibuja el track (el fondo del scrollbar)
    // lo pintamos del color del fondo de la app para que sea transparente
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(ThemeManager.fondo());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
}

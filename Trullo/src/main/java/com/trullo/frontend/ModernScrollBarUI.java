package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;

// el scroll feo de java no va, este es finito, redondeado y con colores del tema
public class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

    // el riel se camufla con el fondo de la app
    @Override
    protected void configureScrollBarColors() {
        trackColor = ThemeManager.fondo();
    }

    // saco las flechitas de arriba y abajo porque nadie las usa
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    // truco: botón 0x0 invisible para ocultar las flechas del scroll
    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    // la barrita que se arrastra, más finita y redondeada
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.scrollThumb());
        // margen de 2px para que no toque los bordes
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y,
                thumbBounds.width - 4, thumbBounds.height, 8, 8);
        g2.dispose(); // libero el g2 del create(), sino pierde memoria
    }

    // el fondo del riel, igual que el fondo general para que no se note
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(ThemeManager.fondo());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
}

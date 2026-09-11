package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;

// el scrollbar feo de java no va, asi que hago uno piola
// mas finito, redondeado y con colores del tema
public class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

    // le digo que el fondo del scroll sea igual al fondo de la app asi se camufla
    @Override
    protected void configureScrollBarColors() {
        trackColor = ThemeManager.fondo();
    }

    // saco las flechitas de arriba y abajo porque son horribles y nadie las usa
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    // boton de 0x0 invisible, truco viejo para ocultar las flechas
    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    // aca dibujo la barrita que se arrastra, la hago redondeada y mas finita
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.scrollThumb());
        // le dejo un margen de 2px a cada lado asi no toca los bordes
        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y,
                thumbBounds.width - 4, thumbBounds.height, 8, 8);
        g2.dispose();
    }

    // el fondo del scroll, lo pinto igual que el fondo general
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(ThemeManager.fondo());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
}

package com.trullo.frontend;

import javax.swing.*;
import java.awt.*;

public class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        trackColor = ThemeManager.fondo();
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(0, 0));
        return btn;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(ThemeManager.scrollThumb());

        g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y,
                thumbBounds.width - 4, thumbBounds.height, 8, 8);
        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(ThemeManager.fondo());
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
}

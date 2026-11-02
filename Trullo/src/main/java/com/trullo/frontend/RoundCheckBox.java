package com.trullo.frontend;

import javax.swing.JCheckBox;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Checkbox redondo dibujado a mano para que combine con el resto de la interfaz.
 */
public class RoundCheckBox extends JCheckBox {

    /**
     * Deja el checkbox sin fondo ni borde nativos y con tamaño fijo de 22x22.
     */
    public RoundCheckBox() {
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(22, 22));
        setMinimumSize(new Dimension(22, 22));
        setMaximumSize(new Dimension(22, 22));
    }

    /**
     * Si está seleccionado pinta el cuadrado azul y el tilde blanco; si no, solo el borde.
     */
    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int size = Math.min(getWidth(), getHeight());
        int originX = (getWidth() - size) / 2;
        int originY = (getHeight() - size) / 2;
        int margin = 2;
        int squareSize = size - margin * 2;

        if (isSelected()) {
            graphics2D.setColor(Theme.accent());
            graphics2D.fillRoundRect(originX + margin, originY + margin, squareSize, squareSize, 8, 8);

            graphics2D.setColor(Color.WHITE);
            graphics2D.setStroke(new BasicStroke(
                    2.4f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));
            int left = originX + margin + 4;
            int top = originY + margin + 4;
            graphics2D.drawLine(
                    left,
                    top + squareSize / 2 - 2,
                    left + squareSize / 3,
                    top + squareSize - 5
            );
            graphics2D.drawLine(
                    left + squareSize / 3,
                    top + squareSize - 5,
                    left + squareSize - 5,
                    top + 4
            );
        } else {
            Color borderColor = getModel().isRollover() ? Theme.accent() : Theme.border();
            graphics2D.setColor(borderColor);
            graphics2D.setStroke(new BasicStroke(2f));
            graphics2D.drawRoundRect(
                    originX + margin,
                    originY + margin,
                    squareSize - 1,
                    squareSize - 1,
                    8,
                    8
            );
        }

        graphics2D.dispose();
    }
}

package com.trullo.frontend;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// medidas y ayuditas compartidas para no repetir números mágicos por todos lados
public class ConstantesUI {

    // anchos y radio que se usan en toda la app, si hay que ajustar se toca acá nomás
    public static final int ANCHO_MENU = 260; // sidebar izquierda
    public static final int ANCHO_ESPACIADOR = 80; // aire de la derecha, es visual nomás
    public static final int RADIO = 14; // qué tan redondeado va todo

    // dibuja un rectángulo redondeado, lo usan casi todas las cards
    public static void paintRoundRect(Graphics g, int w, int h, Color color, int radio) {
        Graphics2D g2 = (Graphics2D) g.create();
        // antialiasing para que no se vea pixelado
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radio, radio));
        g2.dispose(); // libero el g2 del create(), sino pierde memoria
    }

    // que nadie lo instancie, es solo utilidades
    private ConstantesUI() {}
}

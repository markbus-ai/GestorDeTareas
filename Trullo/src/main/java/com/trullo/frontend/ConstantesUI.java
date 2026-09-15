package com.trullo.frontend;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ConstantesUI {

    public static final int ANCHO_MENU = 260;
    public static final int ANCHO_ESPACIADOR = 80;
    public static final int RADIO = 14;

    public static Color fondo()      { return ThemeManager.fondo(); }
    public static Color menu()       { return ThemeManager.menu(); }
    public static Color tarjeta()    { return ThemeManager.tarjeta(); }
    public static Color input()      { return ThemeManager.input(); }
    public static Color borde()      { return ThemeManager.borde(); }
    public static Color texto()      { return ThemeManager.texto(); }
    public static Color textoSuave() { return ThemeManager.textoSuave(); }

    public static void paintRoundRect(Graphics g, int w, int h, Color color, int radio) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radio, radio));
        g2.dispose();
    }

    private ConstantesUI() {}
}

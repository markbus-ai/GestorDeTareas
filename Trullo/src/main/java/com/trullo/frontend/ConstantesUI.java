package com.trullo.frontend;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// clase para no repetir numeros magicos por todos lados
// aca pongo medidas y un helper para dibujar redondeado que uso en todas las cards
public class ConstantesUI {

    // anchos y radios que uso en toda la app, si quiero cambiar algo lo cambio aca nomas
    public static final int ANCHO_MENU = 260;      // el menu de la izquierda
    public static final int ANCHO_ESPACIADOR = 80;  // el espacio vacio de la derecha, es estetico
    public static final int RADIO = 14;             // que tan redondeado es todo

    // estos son atajos para no escribir ThemeManager.blabla() a cada rato
    public static Color fondo()      { return ThemeManager.fondo(); }
    public static Color menu()       { return ThemeManager.menu(); }
    public static Color tarjeta()    { return ThemeManager.tarjeta(); }
    public static Color input()      { return ThemeManager.input(); }
    public static Color borde()      { return ThemeManager.borde(); }
    public static Color texto()      { return ThemeManager.texto(); }
    public static Color textoSuave() { return ThemeManager.textoSuave(); }

    // dibuja un rectangulo redondeado, lo uso en mil lugares
    // le pasas el graphics, tamaño, color y radio y listo
    public static void paintRoundRect(Graphics g, int w, int h, Color color, int radio) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // para que no se vea pixelado
        g2.setColor(color);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radio, radio));
        g2.dispose(); // siempre liberar el g2 sino pierde memoria
    }

    // que nadie instancie esto, es solo utilidades
    private ConstantesUI() {}
}

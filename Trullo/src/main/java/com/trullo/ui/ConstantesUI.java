package com.trullo.ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Clase con constantes y utilidades generales de la UI
// basicamente tiene valores que se usan en toda la app
// y un metodo para pintar rectangulos redondeados
public class ConstantesUI {

    // constantes de diseño que se usan en toda la app
    public static final int ANCHO_MENU = 260;      // ancho del menu lateral en pixeles
    public static final int ANCHO_ESPACIADOR = 80;  // ancho del espaciador de la derecha
    public static final int RADIO = 14;             // radio de curvatura de los bordes redondeados

    // delegadores de ThemeManager para no tener que escribir ThemeManager.cada_vez()
    // son getters que devuelven el color correspondiente al modo actual
    public static Color fondo()      { return ThemeManager.fondo(); }
    public static Color menu()       { return ThemeManager.menu(); }
    public static Color tarjeta()    { return ThemeManager.tarjeta(); }
    public static Color input()      { return ThemeManager.input(); }
    public static Color borde()      { return ThemeManager.borde(); }
    public static Color texto()      { return ThemeManager.texto(); }
    public static Color textoSuave() { return ThemeManager.textoSuave(); }

    // metodo utilitario para pintar un rectangulo redondeado
    // se usa en las tarjetas, cards, y otros componentes que tienen bordes suaves
    // recibe el graphics, ancho, alto, color y radio de curvatura
    public static void paintRoundRect(Graphics g, int w, int h, Color color, int radio) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, radio, radio));
        g2.dispose(); // liberamos recursos del graphics2D
    }

    // constructor privado para que no se pueda instanciar
    // es una clase de utilidad nomas
    private ConstantesUI() {}
}

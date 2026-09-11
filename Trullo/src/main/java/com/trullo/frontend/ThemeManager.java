package com.trullo.frontend;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// este es el que maneja los colores de toda la app
// aca defino los colores de dark y light y despues todos preguntan aca que color usar
// asi no tengo colores hardcodeados por todos lados
public class ThemeManager {

    // solo hay dos modos, oscuro y claro, no hay mas
    public enum Mode { DARK, LIGHT }

    // aca guardo en que modo estoy ahora, arranca en oscuro
    private static Mode currentMode = Mode.DARK;

    // lista de gente que quiere enterarse cuando cambia el tema
    // tipo observer trucho jaja pero funciona
    private static final List<Runnable> listeners = new ArrayList<>();

    public static Mode getMode() { return currentMode; }
    public static boolean isDark() { return currentMode == Mode.DARK; }

    // cambia de uno al otro y avisa a todos
    public static void toggle() {
        currentMode = isDark() ? Mode.LIGHT : Mode.DARK;
        for (Runnable r : listeners) r.run(); // aviso uno por uno
    }

    // por si quiero forzar un modo especifico
    public static void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            for (Runnable r : listeners) r.run();
        }
    }

    // para suscribirse al cambio de tema, le pasas una funcion y listo
    public static void onThemeChange(Runnable listener) {
        listeners.add(listener);
    }

    // paleta dark, estos son los colores que uso en modo oscuro
    private static final Color D_FONDO      = new Color(30, 33, 40);    // fondo general
    private static final Color D_MENU       = new Color(25, 27, 34);    // fondo del menu lateral
    private static final Color D_TARJETA    = new Color(38, 41, 50);    // tarjetas
    private static final Color D_INPUT      = new Color(32, 35, 44);    // inputs
    private static final Color D_BORDE      = new Color(55, 58, 68);    // bordes
    private static final Color D_TEXTO      = new Color(235, 235, 240); // texto principal
    private static final Color D_TEXTO_SUAV = new Color(140, 145, 158); // texto gris clarito

    // paleta light, lo mismo pero para modo claro
    private static final Color L_FONDO      = new Color(244, 245, 248);
    private static final Color L_MENU       = new Color(255, 255, 255);
    private static final Color L_TARJETA    = new Color(255, 255, 255);
    private static final Color L_INPUT      = new Color(240, 242, 246);
    private static final Color L_BORDE      = new Color(220, 224, 232);
    private static final Color L_TEXTO      = new Color(30, 33, 40);
    private static final Color L_TEXTO_SUAV = new Color(120, 125, 138);

    // estos no cambian segun el tema, son los de acento
    public static final Color AZUL       = new Color(88, 130, 247);   // azul principal
    public static final Color AZUL_SUAVE = new Color(88, 130, 247, 35); // azul transparente para seleccionar
    public static final Color VERDE      = new Color(56, 190, 130);   // para el + de notas
    public static final Color NARANJA    = new Color(245, 170, 60);   // para recordatorios
    public static final Color ROSA       = new Color(235, 100, 130);  // para borrar
    public static final Color MORADO    = new Color(160, 100, 230);
    public static final Color CELESTE    = new Color(100, 195, 245);  // para deadlines
    public static final Color LIMA       = new Color(180, 210, 60);

    // estos devuelven el color segun el modo, asi afuera no haces if
    public static Color fondo()      { return isDark() ? D_FONDO      : L_FONDO; }
    public static Color menu()       { return isDark() ? D_MENU       : L_MENU; }
    public static Color tarjeta()    { return isDark() ? D_TARJETA    : L_TARJETA; }
    public static Color input()      { return isDark() ? D_INPUT      : L_INPUT; }
    public static Color borde()      { return isDark() ? D_BORDE      : L_BORDE; }
    public static Color texto()      { return isDark() ? D_TEXTO      : L_TEXTO; }
    public static Color textoSuave() { return isDark() ? D_TEXTO_SUAV : L_TEXTO_SUAV; }

    // colores que calcule a mano para que quede lindo, no los genero dinamico
    private static final Color D_FILTRO   = new Color(55, 58, 64);
    private static final Color L_FILTRO   = new Color(230, 233, 238);
    private static final Color D_HOVER    = new Color(255, 255, 255, 12);
    private static final Color L_HOVER    = new Color(0, 0, 0, 12);
    private static final Color D_ACTIVO   = new Color(88, 130, 247, 30);
    private static final Color L_ACTIVO   = new Color(88, 130, 247, 18);
    private static final Color D_SCROLL   = new Color(80, 85, 100);
    private static final Color L_SCROLL   = new Color(190, 195, 205);
    private static final Color D_DIALOGO  = new Color(45, 48, 54);
    private static final Color L_DIALOGO  = new Color(250, 251, 253);
    private static final Color D_DEADLINE = new Color(40, 60, 100);
    private static final Color L_DEADLINE = new Color(220, 235, 255);

    // gris para filtros desactivados
    public static Color filtroInactivo() {
        return isDark() ? D_FILTRO : L_FILTRO;
    }

    // cuando pasas el mouse por el menu
    public static Color menuHover() {
        return isDark() ? D_HOVER : L_HOVER;
    }

    // cuando el boton del menu esta seleccionado
    public static Color menuActivo() {
        return isDark() ? D_ACTIVO : L_ACTIVO;
    }

    // la barrita del scroll
    public static Color scrollThumb() {
        return isDark() ? D_SCROLL : L_SCROLL;
    }

    // fondo de los dialogs
    public static Color dialogoFondo() {
        return isDark() ? D_DIALOGO : L_DIALOGO;
    }

    // fondo del badge de deadline
    public static Color deadlineBg() {
        return isDark() ? D_DEADLINE : L_DEADLINE;
    }

    // privado para que nadie haga new ThemeManager(), es todo estatico
    private ThemeManager() {}
}

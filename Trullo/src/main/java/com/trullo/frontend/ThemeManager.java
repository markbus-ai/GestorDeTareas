package com.trullo.frontend;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    public enum Mode { DARK, LIGHT }

    private static Mode currentMode = Mode.DARK;

    private static final List<Runnable> listeners = new ArrayList<>();

    public static Mode getMode() { return currentMode; }
    public static boolean isDark() { return currentMode == Mode.DARK; }

    public static void toggle() {
        currentMode = isDark() ? Mode.LIGHT : Mode.DARK;
        for (Runnable r : listeners) r.run();
    }

    public static void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            for (Runnable r : listeners) r.run();
        }
    }

    public static void onThemeChange(Runnable listener) {
        listeners.add(listener);
    }

    private static final Color D_FONDO      = new Color(30, 33, 40);
    private static final Color D_MENU       = new Color(25, 27, 34);
    private static final Color D_TARJETA    = new Color(38, 41, 50);
    private static final Color D_INPUT      = new Color(32, 35, 44);
    private static final Color D_BORDE      = new Color(55, 58, 68);
    private static final Color D_TEXTO      = new Color(235, 235, 240);
    private static final Color D_TEXTO_SUAV = new Color(140, 145, 158);

    private static final Color L_FONDO      = new Color(244, 245, 248);
    private static final Color L_MENU       = new Color(255, 255, 255);
    private static final Color L_TARJETA    = new Color(255, 255, 255);
    private static final Color L_INPUT      = new Color(240, 242, 246);
    private static final Color L_BORDE      = new Color(220, 224, 232);
    private static final Color L_TEXTO      = new Color(30, 33, 40);
    private static final Color L_TEXTO_SUAV = new Color(120, 125, 138);

    public static final Color AZUL       = new Color(88, 130, 247);
    public static final Color AZUL_SUAVE = new Color(88, 130, 247, 35);
    public static final Color VERDE      = new Color(56, 190, 130);
    public static final Color NARANJA    = new Color(245, 170, 60);
    public static final Color ROSA       = new Color(235, 100, 130);
    public static final Color MORADO    = new Color(160, 100, 230);
    public static final Color CELESTE    = new Color(100, 195, 245);
    public static final Color LIMA       = new Color(180, 210, 60);

    public static Color fondo()      { return isDark() ? D_FONDO      : L_FONDO; }
    public static Color menu()       { return isDark() ? D_MENU       : L_MENU; }
    public static Color tarjeta()    { return isDark() ? D_TARJETA    : L_TARJETA; }
    public static Color input()      { return isDark() ? D_INPUT      : L_INPUT; }
    public static Color borde()      { return isDark() ? D_BORDE      : L_BORDE; }
    public static Color texto()      { return isDark() ? D_TEXTO      : L_TEXTO; }
    public static Color textoSuave() { return isDark() ? D_TEXTO_SUAV : L_TEXTO_SUAV; }

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

    public static Color filtroInactivo() {
        return isDark() ? D_FILTRO : L_FILTRO;
    }

    public static Color menuHover() {
        return isDark() ? D_HOVER : L_HOVER;
    }

    public static Color menuActivo() {
        return isDark() ? D_ACTIVO : L_ACTIVO;
    }

    public static Color scrollThumb() {
        return isDark() ? D_SCROLL : L_SCROLL;
    }

    public static Color dialogoFondo() {
        return isDark() ? D_DIALOGO : L_DIALOGO;
    }

    public static Color deadlineBg() {
        return isDark() ? D_DEADLINE : L_DEADLINE;
    }

    private ThemeManager() {}
}

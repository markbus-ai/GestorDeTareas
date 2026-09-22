package com.trullo.frontend;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// paleta central de la app: todos los colores salen de acá para que el dark/light cambie parejo
public class ThemeManager {

    public enum Mode { DARK, LIGHT }

    // arranca en oscuro porque el claro de entrada te quema los ojos
    private static Mode currentMode = Mode.DARK;

    // CopyOnWrite porque se avisa mientras se puede estar agregando/sacando listeners
    private static final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public static Mode getMode() { return currentMode; }
    public static boolean isDark() { return currentMode == Mode.DARK; }

    // cambia de tema y avisa a todos los paneles suscriptos
    public static void toggle() {
        currentMode = isDark() ? Mode.LIGHT : Mode.DARK;
        notificarTema();
    }

    // setea un modo concreto, solo avisa si de verdad cambió
    public static void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            notificarTema();
        }
    }

    // devuelve el mismo listener para poder darlo de baja con offThemeChange
    public static Runnable onThemeChange(Runnable listener) {
        listeners.add(listener);
        return listener;
    }

    // para desuscribirse y no dejar closures muertos dando vueltas
    public static void offThemeChange(Runnable listener) {
        listeners.remove(listener);
    }

    private static void notificarTema() {
        for (Runnable r : listeners) r.run();
    }

    // base del modo oscuro
    private static final Color D_FONDO      = new Color(30, 33, 40);
    private static final Color D_MENU       = new Color(25, 27, 34);
    private static final Color D_TARJETA    = new Color(38, 41, 50);
    private static final Color D_INPUT      = new Color(32, 35, 44);
    private static final Color D_BORDE      = new Color(55, 58, 68);
    private static final Color D_TEXTO      = new Color(235, 235, 240);
    private static final Color D_TEXTO_SUAV = new Color(140, 145, 158);

    // base del modo claro
    private static final Color L_FONDO      = new Color(244, 245, 248);
    private static final Color L_MENU       = new Color(255, 255, 255);
    private static final Color L_TARJETA    = new Color(255, 255, 255);
    private static final Color L_INPUT      = new Color(240, 242, 246);
    // borde un toque más oscuro para que se note sobre fondo claro
    private static final Color L_BORDE      = new Color(206, 211, 221);
    private static final Color L_TEXTO      = new Color(30, 33, 40);
    // texto suave más oscuro para que no se pierda sobre blanco
    private static final Color L_TEXTO_SUAV = new Color(98, 103, 118);

    // acentos que no cambian con el tema
    public static final Color AZUL       = new Color(88, 130, 247);
    // alfa más alto para que el día seleccionado se vea también en modo claro
    public static final Color AZUL_SUAVE = new Color(88, 130, 247, 70);
    public static final Color VERDE      = new Color(56, 190, 130);
    public static final Color NARANJA    = new Color(245, 170, 60);
    public static final Color ROSA       = new Color(235, 100, 130);
    public static final Color MORADO    = new Color(160, 100, 230);
    public static final Color CELESTE    = new Color(100, 195, 245);
    public static final Color LIMA       = new Color(180, 210, 60);
    public static final Color SOL        = new Color(255, 220, 80);

    // atajos para no andar preguntando isDark() por todos lados
    public static Color fondo()      { return isDark() ? D_FONDO      : L_FONDO; }
    public static Color menu()       { return isDark() ? D_MENU       : L_MENU; }
    public static Color tarjeta()    { return isDark() ? D_TARJETA    : L_TARJETA; }
    public static Color input()      { return isDark() ? D_INPUT      : L_INPUT; }
    public static Color borde()      { return isDark() ? D_BORDE      : L_BORDE; }
    public static Color texto()      { return isDark() ? D_TEXTO      : L_TEXTO; }
    public static Color textoSuave() { return isDark() ? D_TEXTO_SUAV : L_TEXTO_SUAV; }

    // estados y superficies secundarias (filtros, hovers, scroll, diálogos)
    private static final Color D_FILTRO   = new Color(55, 58, 64);
    private static final Color L_FILTRO   = new Color(230, 233, 238);
    private static final Color D_HOVER    = new Color(255, 255, 255, 12);
    private static final Color L_HOVER    = new Color(0, 0, 0, 22);
    private static final Color D_ACTIVO   = new Color(88, 130, 247, 30);
    // activo más marcado en claro, con 18 casi no se veía
    private static final Color L_ACTIVO   = new Color(88, 130, 247, 36);
    private static final Color D_SCROLL   = new Color(80, 85, 100);
    private static final Color L_SCROLL   = new Color(175, 180, 192);
    private static final Color D_DIALOGO  = new Color(45, 48, 54);
    private static final Color L_DIALOGO  = new Color(250, 251, 253);
    private static final Color D_DEADLINE = new Color(40, 60, 100);
    private static final Color L_DEADLINE = new Color(210, 228, 248);
    private static final Color D_TOGGLE_OFF = new Color(70, 75, 90);
    private static final Color L_TOGGLE_OFF = new Color(180, 185, 198);

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

    public static Color toggleOff() {
        return isDark() ? D_TOGGLE_OFF : L_TOGGLE_OFF;
    }

    public static Color sol() {
        return SOL;
    }

    // que nadie lo instancie, es todo estático
    private ThemeManager() {}
}

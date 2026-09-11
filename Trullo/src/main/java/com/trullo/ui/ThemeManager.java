package com.trullo.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Esta clase se encarga de manejar los colores del tema
// basicamente tiene todos los colores que usamos en la app
// y permite cambiar entre modo oscuro y claro
public class ThemeManager {

    // los dos modos posibles: oscuro o claro, nothing else matters
    public enum Mode { DARK, LIGHT }

    // aca guardamos en que modo estamos (oscuro o claro)
    private static Mode currentMode = Mode.DARK;

    // lista de listeners que se avisan cuando cambia el tema
    // es como un grupo de WhatsApp pero para eventos
    private static final List<Runnable> listeners = new ArrayList<>();

    // getters para saber en que modo estamos
    public static Mode getMode() { return currentMode; }
    public static boolean isDark() { return currentMode == Mode.DARK; }

    // cambia el tema al contrario (dark->light o light->dark)
    // y avisa a todos los que esten escuchando
    public static void toggle() {
        currentMode = isDark() ? Mode.LIGHT : Mode.DARK;
        for (Runnable r : listeners) r.run(); // avisamos a todos los suscriptores
    }

    // setter para poner un modo especifico
    public static void setMode(Mode mode) {
        if (currentMode != mode) {
            currentMode = mode;
            for (Runnable r : listeners) r.run();
        }
    }

    // registra un listener que se ejecuta cuando cambia el tema
    // es como suscribirse a las notificaciones del tema
    public static void onThemeChange(Runnable listener) {
        listeners.add(listener);
    }

    // ── DARK palette ──
    // todos los colores del modo oscuro, cada uno para su uso
    private static final Color D_FONDO      = new Color(30, 33, 40);    // color de fondo general
    private static final Color D_MENU       = new Color(25, 27, 34);    // color del menu lateral
    private static final Color D_TARJETA    = new Color(38, 41, 50);    // color de las tarjetas/cards
    private static final Color D_INPUT      = new Color(32, 35, 44);    // color de los campos de texto
    private static final Color D_BORDE      = new Color(55, 58, 68);    // color de los bordes
    private static final Color D_TEXTO      = new Color(235, 235, 240); // color del texto principal
    private static final Color D_TEXTO_SUAV = new Color(140, 145, 158); // color del texto secundario

    // ── LIGHT palette ──
    // todos los colores del modo claro, espejo del oscuro basically
    private static final Color L_FONDO      = new Color(244, 245, 248);
    private static final Color L_MENU       = new Color(255, 255, 255);
    private static final Color L_TARJETA    = new Color(255, 255, 255);
    private static final Color L_INPUT      = new Color(240, 242, 246);
    private static final Color L_BORDE      = new Color(220, 224, 232);
    private static final Color L_TEXTO      = new Color(30, 33, 40);
    private static final Color L_TEXTO_SUAV = new Color(120, 125, 138);

    // ── Shared accent colors ──
    // colores de acento que se usan en ambos modos, no cambian
    public static final Color AZUL       = new Color(88, 130, 247);   // el azul principal de la app
    public static final Color AZUL_SUAVE = new Color(88, 130, 247, 35); // azul con transparencia
    public static final Color VERDE      = new Color(56, 190, 130);   // verde para notas y cosas positivas
    public static final Color NARANJA    = new Color(245, 170, 60);   // naranja para recordatorios
    public static final Color ROSA       = new Color(235, 100, 130);  // rosa para hover de borrar
    public static final Color MORADO    = new Color(160, 100, 230);   // morado para las notas
    public static final Color CELESTE    = new Color(100, 195, 245);  // celeste para deadlines
    public static final Color LIMA       = new Color(180, 210, 60);   // lima, por si acaso

    // ── Dynamic getters ──
    // estos metodos devuelven el color correcto segun el modo actual
    // si esta en dark devuelve el color oscuro, si no el claro
    public static Color fondo()      { return isDark() ? D_FONDO      : L_FONDO; }
    public static Color menu()       { return isDark() ? D_MENU       : L_MENU; }
    public static Color tarjeta()    { return isDark() ? D_TARJETA    : L_TARJETA; }
    public static Color input()      { return isDark() ? D_INPUT      : L_INPUT; }
    public static Color borde()      { return isDark() ? D_BORDE      : L_BORDE; }
    public static Color texto()      { return isDark() ? D_TEXTO      : L_TEXTO; }
    public static Color textoSuave() { return isDark() ? D_TEXTO_SUAV : L_TEXTO_SUAV; }

    // colores cacheados para no crear objetos nuevos en cada llamada
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

    // color para los filtros que no estan activos
    public static Color filtroInactivo() {
        return isDark() ? D_FILTRO : L_FILTRO;
    }

    // color de fondo cuando haces hover en un item del menu
    public static Color menuHover() {
        return isDark() ? D_HOVER : L_HOVER;
    }

    // color de fondo del item del menu que esta activo/seleccionado
    public static Color menuActivo() {
        return isDark() ? D_ACTIVO : L_ACTIVO;
    }

    // color del thumb del scrollbar (la bolita que arrastras)
    public static Color scrollThumb() {
        return isDark() ? D_SCROLL : L_SCROLL;
    }

    // color de fondo de los dialogos emergentes
    public static Color dialogoFondo() {
        return isDark() ? D_DIALOGO : L_DIALOGO;
    }

    // color de fondo del badge de deadline
    public static Color deadlineBg() {
        return isDark() ? D_DEADLINE : L_DEADLINE;
    }

    // constructor privado para que no puedan instanciar esta clase
    // es una clase estatica, solo tiene metodos estaticos
    private ThemeManager() {}
}

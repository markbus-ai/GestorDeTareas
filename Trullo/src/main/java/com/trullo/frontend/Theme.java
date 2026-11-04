package com.trullo.frontend;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Paleta central y notificador de tema para toda la aplicación.
 *
 * <p>Todas las pantallas piden sus colores a esta clase, entonces cambiar entre modo oscuro y
 * claro actualiza toda la interfaz desde un solo lugar. También configura el estilo global para que
 * siempre coincida con la paleta activa.
 */
public final class Theme {

    // Los dos modos disponibles de la aplicación.
    public enum Mode {
        DARK,
        LIGHT
    }

    // Lista de callbacks que se avisan cuando cambia el tema (pantallas, botones, labels, etc.).
    private static final List<Runnable> THEME_LISTENERS = new CopyOnWriteArrayList<>();

    // Modo que está activo ahora. Arranca en oscuro.
    private static Mode currentMode = Mode.DARK;

    // Colores fijos del modo oscuro.
    private static final Color DARK_BACKGROUND = new Color(30, 33, 40);
    private static final Color DARK_SIDEBAR = new Color(25, 27, 34);
    private static final Color DARK_SURFACE = new Color(38, 41, 50);
    private static final Color DARK_SURFACE_ALT = new Color(44, 48, 58);
    private static final Color DARK_INPUT = new Color(32, 35, 44);
    private static final Color DARK_BORDER = new Color(55, 58, 68);
    private static final Color DARK_TEXT = new Color(235, 235, 240);
    private static final Color DARK_MUTED_TEXT = new Color(140, 145, 158);
    private static final Color DARK_SUBTLE = new Color(55, 58, 64);
    private static final Color DARK_HOVER = new Color(255, 255, 255, 14);
    private static final Color DARK_ACTIVE = new Color(88, 130, 247, 34);
    private static final Color DARK_SCROLL_THUMB = new Color(80, 85, 100);
    private static final Color DARK_DIALOG = new Color(45, 48, 54);
    private static final Color DARK_DEADLINE = new Color(40, 60, 100);
    private static final Color DARK_SWITCH_OFF = new Color(70, 75, 90);

    // Colores fijos del modo claro.
    private static final Color LIGHT_BACKGROUND = new Color(244, 245, 248);
    private static final Color LIGHT_SIDEBAR = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE = new Color(255, 255, 255);
    private static final Color LIGHT_SURFACE_ALT = new Color(248, 249, 252);
    private static final Color LIGHT_INPUT = new Color(240, 242, 246);
    private static final Color LIGHT_BORDER = new Color(206, 211, 221);
    private static final Color LIGHT_TEXT = new Color(30, 33, 40);
    private static final Color LIGHT_MUTED_TEXT = new Color(98, 103, 118);
    private static final Color LIGHT_SUBTLE = new Color(230, 233, 238);
    private static final Color LIGHT_HOVER = new Color(0, 0, 0, 20);
    private static final Color LIGHT_ACTIVE = new Color(88, 130, 247, 36);
    private static final Color LIGHT_SCROLL_THUMB = new Color(175, 180, 192);
    private static final Color LIGHT_DIALOG = new Color(250, 251, 253);
    private static final Color LIGHT_DEADLINE = new Color(210, 228, 248);
    private static final Color LIGHT_SWITCH_OFF = new Color(180, 185, 198);

    // Colores de acento y estados, iguales en ambos temas.
    public static final Color ACCENT = new Color(88, 130, 247);
    public static final Color ACCENT_SOFT = new Color(88, 130, 247, 72);
    public static final Color SUCCESS = new Color(56, 190, 130);
    public static final Color WARNING = new Color(245, 170, 60);
    public static final Color DANGER = new Color(235, 100, 130);
    public static final Color PURPLE = new Color(160, 100, 230);
    public static final Color INFO = new Color(100, 195, 245);
    public static final Color LIME = new Color(180, 210, 60);
    public static final Color SUN = new Color(255, 220, 80);

    /**
     * Clase de utilidades: no se instancia.
     */
    private Theme() {
    }

    /**
     * Devuelve el modo actual (DARK o LIGHT).
     */
    public static Mode getMode() {
        return currentMode;
    }

    /**
     * Atajo para preguntar si ahora mismo está activo el modo oscuro.
     */
    public static boolean isDark() {
        return currentMode == Mode.DARK;
    }

    /**
     * Cambia el modo si es distinto al actual y avisa a todos los componentes registrados.
     */
    public static void setMode(Mode mode) {
        if (mode != null && currentMode != mode) {
            currentMode = mode;
            notifyListeners();
        }
    }

    /**
     * Alterna entre oscuro y claro y notifica el cambio.
     */
    public static void toggle() {
        currentMode = isDark() ? Mode.LIGHT : Mode.DARK;
        notifyListeners();
    }

    /**
     * Registra un callback para que se ejecute en cada cambio de tema. Devuelve el mismo
     * listener para poder guardarlo o quitarlo después.
     */
    public static Runnable addListener(Runnable listener) {
        THEME_LISTENERS.add(listener);
        return listener;
    }

    /**
     * Quita un listener que ya no se quiere seguir notificando.
     */
    public static void removeListener(Runnable listener) {
        THEME_LISTENERS.remove(listener);
    }

    /**
     * Recorre la lista y ejecuta cada callback (repintar, reaplicar colores, etc.).
     */
    private static void notifyListeners() {
        for (Runnable listener : THEME_LISTENERS) {
            listener.run();
        }
    }

    /**
     * Aplica el estilo visual que corresponde a la paleta actual y deja configurados
     * algunos valores globales que comparten todas las pantallas (bordes redondeados, scrollbars,
     * colores de los diálogos, fuente base).
     */
    public static void applyLookAndFeel() {
        // Elige el estilo oscuro o claro según el modo activo.
        if (isDark()) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }

        // Ajustes globales de estilo para que los controles se vean como el resto de la UI.
        UIManager.put("Component.focusWidth", 0);
        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 14);
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("OptionPane.background", dialogBackground());
        UIManager.put("OptionPane.messageForeground", text());
        UIManager.put("Label.font", new Font(UiKit.FONT_FAMILY, Font.PLAIN, 14));
    }

    /**
     * Fondo general de la ventana.
     */
    public static Color background() {
        return isDark() ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    /**
     * Fondo de la barra lateral de navegación.
     */
    public static Color sidebar() {
        return isDark() ? DARK_SIDEBAR : LIGHT_SIDEBAR;
    }

    /**
     * Color de las tarjetas y superficies elevadas.
     */
    public static Color surface() {
        return isDark() ? DARK_SURFACE : LIGHT_SURFACE;
    }

    /**
     * Variante de superficie para filas y bloques secundarios.
     */
    public static Color surfaceAlt() {
        return isDark() ? DARK_SURFACE_ALT : LIGHT_SURFACE_ALT;
    }

    /**
     * Fondo de los campos de texto y combos.
     */
    public static Color input() {
        return isDark() ? DARK_INPUT : LIGHT_INPUT;
    }

    /**
     * Color de bordes y separadores.
     */
    public static Color border() {
        return isDark() ? DARK_BORDER : LIGHT_BORDER;
    }

    /**
     * Color principal del texto.
     */
    public static Color text() {
        return isDark() ? DARK_TEXT : LIGHT_TEXT;
    }

    /**
     * Color del texto secundario o menos importante.
     */
    public static Color mutedText() {
        return isDark() ? DARK_MUTED_TEXT : LIGHT_MUTED_TEXT;
    }

    /**
     * Fondo apenas más claro/oscuro que la superficie, para chips y estados vacíos.
     */
    public static Color subtleBackground() {
        return isDark() ? DARK_SUBTLE : LIGHT_SUBTLE;
    }

    /**
     * Capa transparente que se pinta cuando el mouse pasa por encima de un botón.
     */
    public static Color hoverOverlay() {
        return isDark() ? DARK_HOVER : LIGHT_HOVER;
    }

    /**
     * Capa transparente del ítem activo del menú lateral.
     */
    public static Color activeOverlay() {
        return isDark() ? DARK_ACTIVE : LIGHT_ACTIVE;
    }

    /**
     * Color de la barra de scroll (el "thumb").
     */
    public static Color scrollThumb() {
        return isDark() ? DARK_SCROLL_THUMB : LIGHT_SCROLL_THUMB;
    }

    /**
     * Fondo de los diálogos personalizados.
     */
    public static Color dialogBackground() {
        return isDark() ? DARK_DIALOG : LIGHT_DIALOG;
    }

    /**
     * Fondo de la etiqueta que muestra la fecha límite de una tarea.
     */
    public static Color deadlineBackground() {
        return isDark() ? DARK_DEADLINE : LIGHT_DEADLINE;
    }

    /**
     * Color del switch cuando está apagado.
     */
    public static Color switchOff() {
        return isDark() ? DARK_SWITCH_OFF : LIGHT_SWITCH_OFF;
    }

    /**
     * Color de acento principal (azul).
     */
    public static Color accent() {
        return ACCENT;
    }

    /**
     * Devuelve una copia del color con otra transparencia (alpha de 0 a 255).
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}

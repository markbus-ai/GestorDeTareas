package com.trullo.frontend;

/**
 * Destinos de la barra lateral. Usar un enum evita comparar strings sueltos por todo el código
 * de navegación.
 */
public enum NavigationSection {

    /**
     * Cada constante guarda el título que se ve en el menú y el ícono que lo acompaña.
     */
    HOME("Inicio", AppIcon.Glyph.HOME),
    TASKS("Mis tareas", AppIcon.Glyph.TASKS),
    PROJECTS("Proyectos", AppIcon.Glyph.FOLDER),
    CALENDAR("Calendario", AppIcon.Glyph.CALENDAR),
    NOTES("Notas", AppIcon.Glyph.NOTES),
    SETTINGS("Configuración", AppIcon.Glyph.SETTINGS);

    private final String title;
    private final AppIcon.Glyph icon;

    /**
     * Constructor del enum.
     */
    NavigationSection(String title, AppIcon.Glyph icon) {
        this.title = title;
        this.icon = icon;
    }

    /**
     * Texto que se muestra en el botón de la barra lateral.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Ícono vectorial asociado a la sección.
     */
    public AppIcon.Glyph getIcon() {
        return icon;
    }
}

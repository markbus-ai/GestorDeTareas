package com.trullo.frontend;

/**
 * Lo implementan las pantallas que necesitan cargar o refrescar datos cada vez que se muestran
 * (por ejemplo, tareas y notas leídas de la base).
 */
public interface RefreshableScreen {

    /**
     * MainWindow llama a este método al mostrar la pantalla.
     */
    void onShown();
}

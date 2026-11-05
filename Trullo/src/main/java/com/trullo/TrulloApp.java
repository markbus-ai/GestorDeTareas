package com.trullo;

import com.trullo.bootstrap.DependenceContainer;
import com.trullo.controller.NoteController;
import com.trullo.controller.ProyectoController;
import com.trullo.controller.ReminderController;
import com.trullo.controller.ReminderTargetController;
import com.trullo.controller.TaskController;
import com.trullo.core.Container;
import com.trullo.core.ErrorReporter;
import com.trullo.frontend.MainWindow;
import com.trullo.frontend.Theme;
import com.trullo.notification.ReminderNotifications;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.awt.GraphicsEnvironment;
import java.util.Locale;

/**
 * Punto de entrada de Trullo. Aplica el tema visual, arma el contenedor de dependencias
 * y abre la ventana principal en el hilo de eventos de la interfaz.
 */
public final class TrulloApp {

    /**
     * Constructor privado: esta clase solo se usa de forma estática.
     */
    private TrulloApp() {
    }

    /**
     * Arranca la aplicación: configura el tema y construye la ventana principal en el hilo de
     * eventos de la interfaz.
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            String message = ErrorReporter.report("Error no controlado en " + thread.getName(), error);
            if (!GraphicsEnvironment.isHeadless()) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        null, message, "Error de Trullo", JOptionPane.ERROR_MESSAGE));
            }
        });
        // Fija el español como idioma por defecto para fechas y textos de los diálogos.
        Locale.setDefault(Locale.forLanguageTag("es-ES"));

        // Primero deja listo el estilo visual con los colores del tema actual (oscuro por defecto).
        Theme.applyLookAndFeel();

        // Arma el contenedor con los DAOs, services y controllers registrados.
        Container container = DependenceContainer.create();

        // Envío de recordatorios en segundo plano por los canales configurados en el .env.
        ReminderNotifications.start(container);

        // Todo lo visual se construye dentro del hilo de eventos de la interfaz.
        SwingUtilities.invokeLater(() -> {
            // El contenedor entrega los controladores ya armados con sus dependencias.
            TaskController taskController = container.resolve(TaskController.class);
            NoteController noteController = container.resolve(NoteController.class);
            ReminderController reminderController = container.resolve(ReminderController.class);
            ReminderTargetController reminderTargetController =
                    container.resolve(ReminderTargetController.class);
            ProyectoController proyectoController = container.resolve(ProyectoController.class);

            // La ventana recibe los controladores y arma las pantallas con ellos.
            MainWindow mainWindow = new MainWindow(
                    taskController,
                    noteController,
                    reminderController,
                    reminderTargetController,
                    proyectoController
            );
            mainWindow.setVisible(true);
        });
    }
}

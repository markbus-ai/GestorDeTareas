package com.trullo.bootstrap;

import com.trullo.core.Container;
import com.trullo.controller.TaskController;
import com.trullo.controller.LabelController;
import com.trullo.controller.NoteController;
import com.trullo.controller.ProyectoController;
import com.trullo.controller.ReminderController;
import com.trullo.interfaces.services.LabelService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.interfaces.services.NoteService;
import com.trullo.interfaces.services.ProyectoService;
import com.trullo.interfaces.services.ReminderService;

/**
 * Registra los controladores como dependencias transitorias.
 *
 * <p>Cada controller se crea nuevo en cada {@code resolve}, porque coordina una
 * operación puntual entre la vista y el servicio y no necesita conservar estado.
 * Debe aplicarse después de {@link ServiceModule}.
 */
public final class ControllerModule implements Module {

    /**
     * Asocia cada controller con su implementación, inyectando el servicio
     * correspondiente.
     *
     * @param container contenedor donde se registran los controllers
     */
    @Override
    public void register(Container container) {
        container.registerTransient(
            TaskController.class,
            () -> new TaskController(
                container.resolve(TaskService.class)
            )
        );
        container.registerTransient(
            LabelController.class, 
            () -> new LabelController(
                container.resolve(LabelService.class)
            )
        );
        container.registerTransient(
            NoteController.class,
            () -> new NoteController(
                container.resolve(NoteService.class)
            )
        );
        container.registerTransient(
            ProyectoController.class, 
            () -> new ProyectoController(
                container.resolve(ProyectoService.class)
            )
        );
        container.registerTransient(
            ReminderController.class,
            () -> new ReminderController(
                container.resolve(ReminderService.class)
            )
        );
    }
}
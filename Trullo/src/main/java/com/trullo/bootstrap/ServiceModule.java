package com.trullo.bootstrap;

import com.trullo.core.Container;
import com.trullo.interfaces.dao.LabelDAOInterface;
import com.trullo.interfaces.dao.ReminderDAOInterface;
import com.trullo.interfaces.dao.TaskDAOInterface;
import com.trullo.interfaces.dao.TaskLabelDAOInterface;
import com.trullo.interfaces.dao.ProjectDAOInterface;
import com.trullo.interfaces.dao.NoteDAOInterface;
import com.trullo.interfaces.services.LabelService;
import com.trullo.interfaces.services.TaskService;
import com.trullo.interfaces.services.NoteService;
import com.trullo.interfaces.services.ProyectoService;
import com.trullo.interfaces.services.ReminderService;
import com.trullo.services.LabelServiceImpl;
import com.trullo.services.TaskServiceImpl;
import com.trullo.services.NoteServiceImpl;
import com.trullo.services.ProyectoServiceImpl;
import com.trullo.services.ReminderServiceImpl;

/**
 * Registra los servicios de negocio como singletons.
 *
 * <p>Cada servicio se construye resolviendo desde el contenedor los DAOs que necesita,
 * por lo que este módulo debe aplicarse después de {@link DAOModule}. Los servicios
 * no dependen del estado de la interfaz y se reutilizan durante toda la ejecución.
 */
public final class ServiceModule implements Module {

    /**
     * Asocia cada interfaz de servicio con su implementación concreta, inyectando
     * los DAOs requeridos por constructor.
     *
     * @param container contenedor donde se registran los servicios
     */
    @Override
    public void register(Container container) {
        container.registerSingleton (
            TaskService.class, 
            () -> new TaskServiceImpl(
                container.resolve(TaskDAOInterface.class),
                container.resolve(LabelDAOInterface.class),
                container.resolve(TaskLabelDAOInterface.class)
            )
        );

        container.registerSingleton (
            LabelService.class, 
            () -> new LabelServiceImpl(
                container.resolve(LabelDAOInterface.class)
            )
        );

        container.registerSingleton (
            ReminderService.class, 
            () -> new ReminderServiceImpl(
                container.resolve(ReminderDAOInterface.class)
            )
        );
        
        container.registerSingleton(
            ProyectoService.class,
            () -> new ProyectoServiceImpl(
                container.resolve(ProjectDAOInterface.class)
            )
        );

        container.registerSingleton(
            NoteService.class,
            () -> new NoteServiceImpl(
                container.resolve(NoteDAOInterface.class)
            )
        );
    }
}

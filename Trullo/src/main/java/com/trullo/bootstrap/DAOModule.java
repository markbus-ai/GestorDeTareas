package com.trullo.bootstrap;

import com.trullo.core.Container;
import com.trullo.interfaces.dao.TaskDAOInterface;
import com.trullo.interfaces.dao.LabelDAOInterface;
import com.trullo.interfaces.dao.TaskLabelDAOInterface;
import com.trullo.interfaces.dao.NoteDAOInterface;
import com.trullo.interfaces.dao.ProjectDAOInterface;
import com.trullo.interfaces.dao.ReminderDAOInterface;
import com.trullo.dao.TaskDAO;
import com.trullo.dao.LabelDAO;
import com.trullo.dao.TaskLabelDAO;
import com.trullo.dao.NoteDAO;
import com.trullo.dao.ProjectDAO;
import com.trullo.dao.ReminderDAO;

/**
 * Registra los DAOs de persistencia como singletons.
 *
 * <p>Asocia cada interfaz de {@code com.trullo.interfaces.dao} con su implementación
 * concreta en {@code com.trullo.dao}, para que services y controllers dependan de las
 * abstracciones. Como los DAOs no conservan estado entre operaciones, se comparten
 * como una única instancia.
 */
public final class DAOModule implements Module {

    /**
     * Asocia cada interfaz de DAO con su implementación concreta.
     *
     * @param c contenedor donde se registran los DAOs
     */
    @Override
    public void register(Container c) {
        c.registerSingleton(TaskDAOInterface.class, TaskDAO::new);
        c.registerSingleton(LabelDAOInterface.class, LabelDAO::new);
        c.registerSingleton(TaskLabelDAOInterface.class, TaskLabelDAO::new);
        c.registerSingleton(NoteDAOInterface.class, NoteDAO::new);
        c.registerSingleton(ProjectDAOInterface.class, ProjectDAO::new);
        c.registerSingleton(ReminderDAOInterface.class, ReminderDAO::new);
    }
}
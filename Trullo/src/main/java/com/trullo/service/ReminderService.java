package com.trullo.service;

import com.trullo.model.Reminder;
import java.util.List;

public interface ReminderService {
    Reminder crear(Reminder reminder);

    Reminder modificar(Reminder reminder);

    void eliminar(Long id);

    Reminder obtenerPorId(Long id);

    List<Reminder> listar();
}

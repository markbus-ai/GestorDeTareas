package com.trullo.interfaces.services;

import com.trullo.model.Reminder;
import java.util.List;

/**
 * Servicio de recordatorios de la aplicación.
 *
 * <p>Define las operaciones de negocio de alta, baja, modificación y consulta de recordatorios.
 * Las implementaciones se encargan de coordinar la persistencia a través de la capa DAO.
 */
public interface ReminderService {
    /**
     * Crea un recordatorio nuevo.
     *
     * @param reminder el recordatorio a crear
     * @return el recordatorio creado, con su identificador asignado
     */
    Reminder crear(Reminder reminder);

    /**
     * Modifica un recordatorio existente.
     *
     * @param reminder el recordatorio con los datos actualizados; debe tener identificador
     * @return el recordatorio modificado
     */
    Reminder modificar(Reminder reminder);

    /**
     * Elimina un recordatorio por su identificador.
     *
     * @param id el identificador del recordatorio a eliminar
     */
    void eliminar(Long id);

    /**
     * Obtiene un recordatorio por su identificador.
     *
     * @param id el identificador del recordatorio
     * @return el recordatorio encontrado
     */
    Reminder obtenerPorId(Long id);

    /**
     * Lista todos los recordatorios.
     *
     * @return la lista de recordatorios; vacía si no hay ninguno
     */
    List<Reminder> listar();

    /**
     * Lista los recordatorios vencidos que todavía no se enviaron.
     *
     * @return los recordatorios pendientes de envío; vacía si no hay ninguno
     */
    List<Reminder> listarPendientes();
}

package com.trullo.interfaces.services;

import com.trullo.model.Reminder;
import com.trullo.model.ReminderTarget;
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
     * Agrega un destino de envío a un recordatorio existente.
     *
     * @param reminderId el identificador del recordatorio
     * @param channel el identificador del canal, por ejemplo {@code whatsapp} o {@code email}
     * @param address la dirección de destino dentro del canal
     * @return el destino creado, con su identificador asignado
     */
    ReminderTarget agregarDestino(Long reminderId, String channel, String address);

    /**
     * Lista los destinos de un recordatorio.
     *
     * @param reminderId el identificador del recordatorio
     * @return los destinos del recordatorio; vacía si no tiene ninguno
     */
    List<ReminderTarget> listarDestinos(Long reminderId);

    /**
     * Lista los destinos de recordatorios vencidos que todavía no se enviaron.
     *
     * @return los destinos pendientes de envío; vacía si no hay ninguno
     */
    List<ReminderTarget> listarDestinosPendientes();

    /**
     * Marca un destino como enviado.
     *
     * @param targetId el identificador del destino
     */
    void marcarEnviado(Long targetId);
}

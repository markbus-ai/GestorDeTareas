package com.trullo.interfaces.services;

import com.trullo.model.ReminderTarget;
import java.util.List;

/**
 * Servicio de destinos de recordatorios.
 *
 * <p>Un destino indica por qué canal y a qué dirección se envía un recordatorio. Las
 * implementaciones se encargan de coordinar la persistencia a través de la capa DAO.
 */
public interface ReminderTargetService {
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

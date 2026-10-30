package com.trullo.interfaces.dao;

import com.trullo.interfaces.BaseInterface;
import com.trullo.model.ReminderTarget;
import java.util.List;

/**
 * Define las operaciones de acceso a datos para los destinos de los recordatorios.
 * <p>
 * Además del CRUD heredado de {@link BaseInterface}, permite consultar los destinos
 * de un recordatorio, los que están pendientes de envío y marcarlos como enviados.
 */
public interface ReminderTargetDAOInterface extends BaseInterface<ReminderTarget> {

    /**
     * Busca los destinos de un recordatorio.
     *
     * @param reminderId identificador del recordatorio
     * @return lista de destinos del recordatorio; vacía si no hay ninguno
     */
    List<ReminderTarget> findByReminderId(Long reminderId);

    /**
     * Obtiene los destinos que aún no fueron enviados y cuyo recordatorio
     * ya llegó a su hora programada.
     *
     * @return lista de destinos pendientes; vacía si no hay ninguno
     */
    List<ReminderTarget> findPending();

    /**
     * Marca un destino como enviado con la fecha y hora actual.
     *
     * @param id identificador del destino
     */
    void markSent(Long id);
}

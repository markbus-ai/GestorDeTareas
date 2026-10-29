package com.trullo.exception;

/**
 * Excepción lanzada cuando se solicita una entidad que no existe en la base de datos.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje descriptivo.
     *
     * @param message el mensaje que describe la entidad no encontrada
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Crea la excepción para una entidad identificada por su nombre e identificador.
     *
     * @param entidad el nombre de la entidad buscada, por ejemplo {@code "Tarea"}
     * @param id el identificador buscado
     * @return la excepción con el mensaje armado
     */
    public static NotFoundException of(String entidad, Long id) {
        return new NotFoundException(entidad + " con id " + id + " no encontrada");
    }

    /**
     * Devuelve la entidad si existe o lanza la excepción si es {@code null}.
     *
     * @param <T> el tipo de la entidad
     * @param entity la entidad obtenida del DAO, posiblemente {@code null}
     * @param entidad el nombre de la entidad buscada, para el mensaje de error
     * @param id el identificador buscado, para el mensaje de error
     * @return la entidad, nunca {@code null}
     * @throws NotFoundException si {@code entity} es {@code null}
     */
    public static <T> T requireFound(T entity, String entidad, Long id) {
        if (entity == null) {
            throw of(entidad, id);
        }
        return entity;
    }
}

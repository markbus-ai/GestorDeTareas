package com.trullo.bootstrap;

import com.trullo.core.Container;

/**
 * Contrato para agrupar el registro de dependencias por capa.
 *
 * <p>Cada implementación recibe un {@link Container} y registra sus bindings.
 * {@link DependenceContainer} aplica los módulos en orden al iniciar la aplicación.
 */
public interface Module {

    /**
     * Registra las dependencias de la capa representada por este módulo.
     *
     * @param container contenedor donde se agregan los bindings
     */
    void register(Container container);
}

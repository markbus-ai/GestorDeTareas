package com.trullo.bootstrap;

import com.trullo.core.Container;

/**
 * Punto de armado del contenedor de dependencias de la aplicación.
 *
 * <p>Registra los módulos en orden de capa: primero los DAOs, luego los services y
 * por último los controllers. Así cada módulo puede resolver las dependencias
 * registradas por los anteriores.
 */
public final class DependenceContainer {

    private DependenceContainer() {}

    /**
     * Crea el contenedor con todos los módulos de la aplicación registrados.
     *
     * @return el contenedor listo para resolver DAOs, services y controllers
     */
    public static Container create() {
        Container c = new Container();
        new DAOModule().register(c);
        new ServiceModule().register(c);
        new ControllerModule().register(c);
        return c;
    }
}
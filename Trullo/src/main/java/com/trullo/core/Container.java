package com.trullo.core;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;


/**
 * Contenedor de inyección de dependencias mínimo basado en constructores.
 *
 * <p>Permite registrar proveedores ({@link Supplier}) por tipo y resolver instancias
 * por interfaz. Los bindings registrados con {@link #registerTransient(Class, Supplier)}
 * generan una instancia nueva en cada resolución; los registrados con
 * {@link #registerSingleton(Class, Supplier)} se crean una sola vez y se reutilizan.
 * Si un tipo no fue registrado, se intenta construir por reflexión usando su primer
 * constructor.
 */
public class Container {
    private final Map<Class<?>, Supplier<?>> bindings = new HashMap<>();
    private final Set<Class<?>> singletonTypes = new HashSet<>();
    private final Map<Class<?>, Object> instances = new HashMap<>();

    /**
     * Registra un proveedor cuyo resultado es una instancia nueva en cada resolución.
     *
     * @param <T> tipo de la dependencia
     * @param type tipo (normalmente una interfaz) que se quiere resolver
     * @param provider fábrica que crea la instancia
     */
    public <T> void registerTransient(Class<T> type, Supplier<T> provider) {
        bindings.put(type, provider);
    }

    /**
     * Registra un proveedor cuya primera instancia se conserva y se reutiliza en
     * todas las resoluciones posteriores.
     *
     * @param <T> tipo de la dependencia
     * @param type tipo (normalmente una interfaz) que se quiere resolver
     * @param provider fábrica que crea la instancia
     */
    public <T> void registerSingleton(Class<T> type, Supplier<T> provider) {
        bindings.put(type, provider);
        singletonTypes.add(type);
    }

    /**
     * Resuelve una dependencia por su tipo.
     *
     * <p>Si el tipo fue registrado como singleton y ya existe una instancia, la devuelve.
     * En caso contrario ejecuta el proveedor registrado o, si no hay binding, construye
     * el objeto por reflexión.
     *
     * @param <T> tipo de la dependencia
     * @param type tipo que se desea resolver
     * @return la instancia correspondiente al tipo solicitado
     * @throws IllegalStateException si la construcción por reflexión falla
     */
    public <T> T resolve(Class<T> type) {
        if (instances.containsKey(type)) {
            return type.cast(instances.get(type));
        }

        Supplier<?> provider = bindings.get(type);
        Object instance = (provider != null) ? provider.get() : construct(type);

        if (singletonTypes.contains(type)) {
            instances.put(type, instance);
        }
        return type.cast(instance);
    }

    /**
     * Construye una instancia por reflexión usando el primer constructor declarado,
     * resolviendo recursivamente cada uno de sus parámetros.
     *
     * @param <T> tipo de la dependencia
     * @param type tipo que se desea construir
     * @return la instancia creada
     * @throws IllegalStateException si el constructor no puede invocarse
     */
    private <T> T construct(Class<T> type) {
        Constructor<?> ctor = type.getDeclaredConstructors()[0];
        Object[] args = Arrays.stream(ctor.getParameterTypes())
                .map(this::resolve)
                .toArray();
        try {
            return type.cast(ctor.newInstance(args));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No se pudo crear " + type.getName(), e);
        }
    }
}
package com.roberto.automatizacion.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gestor de configuración del framework
 * Principios aplicados: Singleton, Encapsulación
 *
 * @author Roberto Rivas Lopez
 */
public final class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static volatile ConfigManager instancia;
    private final Properties propiedades;

    // Valores por defecto
    private static final String NAVEGADOR_DEFECTO = "chrome";
    private static final String ENTORNO_DEFECTO = "desarrollo";
    private static final int TIMEOUT_DEFECTO = 30;
    private static final boolean HEADLESS_DEFECTO = false;

    private ConfigManager() {
        propiedades = new Properties();
        cargarConfiguracion();
    }

    public static ConfigManager getInstancia() {
        if (instancia == null) {
            synchronized (ConfigManager.class) {
                if (instancia == null) {
                    instancia = new ConfigManager();
                }
            }
        }
        return instancia;
    }

    private void cargarConfiguracion() {
        // Cargar desde archivo de propiedades
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                propiedades.load(input);
                logger.info("Configuración cargada desde config.properties");
            } else {
                logger.warn("config.properties no encontrado, usando valores por defecto");
            }
        } catch (IOException e) {
            logger.error("Error cargando configuración: {}", e.getMessage());
        }

        // Sobrescribir con propiedades del sistema
        propiedades.putAll(System.getProperties());

        // Sobrescribir con variables de entorno
        System.getenv().forEach((key, value) -> {
            String propertyKey = key.toLowerCase().replace('_', '.');
            propiedades.setProperty(propertyKey, value);
        });
    }

    public String obtenerNavegador() {
        return propiedades.getProperty("navegador", NAVEGADOR_DEFECTO);
    }

    public String obtenerEntorno() {
        return propiedades.getProperty("entorno", ENTORNO_DEFECTO);
    }

    public int obtenerTimeout() {
        String timeout = propiedades.getProperty("timeout", String.valueOf(TIMEOUT_DEFECTO));
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            logger.warn("Timeout inválido '{}', usando valor por defecto: {}", timeout, TIMEOUT_DEFECTO);
            return TIMEOUT_DEFECTO;
        }
    }

    public boolean esHeadless() {
        return Boolean.parseBoolean(propiedades.getProperty("headless", String.valueOf(HEADLESS_DEFECTO)));
    }

    public boolean esEjecucionRemota() {
        return Boolean.parseBoolean(propiedades.getProperty("ejecucion.remota", "false"));
    }

    public String obtenerUrlRemota() {
        return propiedades.getProperty("url.remota", "http://localhost:4444/wd/hub");
    }

    public String obtenerUrlBase() {
        return propiedades.getProperty("url.base", "https://example.com");
    }
}
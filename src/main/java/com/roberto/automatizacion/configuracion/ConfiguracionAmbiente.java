package com.roberto.automatizacion.configuracion;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Gestiona la configuración del ambiente de pruebas
 */
public class ConfiguracionAmbiente {
    
    private static Properties propiedades;
    private static final String ARCHIVO_CONFIG = "src/main/resources/configuraciones/config.properties";
    
    static {
        cargarPropiedades();
    }
    
    private static void cargarPropiedades() {
        propiedades = new Properties();
        try (FileInputStream archivo = new FileInputStream(ARCHIVO_CONFIG)) {
            propiedades.load(archivo);
        } catch (IOException e) {
            System.err.println("Error cargando configuración: " + e.getMessage());
            // Configuración por defecto
            propiedades.setProperty("url.base", "https://www.google.com");
            propiedades.setProperty("navegador", "chrome");
            propiedades.setProperty("timeout.implicito", "10");
            propiedades.setProperty("timeout.explicito", "30");
        }
    }
    
    public static String obtenerUrlBase() {
        return propiedades.getProperty("url.base", "https://www.google.com");
    }
    
    public static String obtenerNavegador() {
        return System.getProperty("navegador", propiedades.getProperty("navegador", "chrome"));
    }
    
    public static int obtenerTimeoutImplicito() {
        return Integer.parseInt(propiedades.getProperty("timeout.implicito", "10"));
    }
    
    public static int obtenerTimeoutExplicito() {
        return Integer.parseInt(propiedades.getProperty("timeout.explicito", "30"));
    }
    
    public static String obtenerPropiedad(String clave) {
        return propiedades.getProperty(clave);
    }
}

package com.roberto.automatizacion.utilidades;

import com.roberto.automatizacion.factory.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para captura de pantallas
 */
public class UtilidadesScreenshot {
    
    private static final String DIRECTORIO_SCREENSHOTS = "reportes/screenshots/";
    
    static {
        // Crear directorio si no existe
        new File(DIRECTORIO_SCREENSHOTS).mkdirs();
    }
    
    public static String tomarScreenshot(String nombrePrueba) throws IOException {
        WebDriver driver = DriverFactory.obtenerDriver();
        if (driver == null) {
            throw new IllegalStateException("Driver no inicializado");
        }
        
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File archivoOrigen = takesScreenshot.getScreenshotAs(OutputType.FILE);
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = nombrePrueba + "_" + timestamp + ".png";
        String rutaCompleta = DIRECTORIO_SCREENSHOTS + nombreArchivo;
        
        File archivoDestino = new File(rutaCompleta);
        FileUtils.copyFile(archivoOrigen, archivoDestino);
        
        System.out.println("📸 Screenshot guardado: " + rutaCompleta);
        return rutaCompleta;
    }
    
    public static String tomarScreenshotConRutaPersonalizada(String nombreArchivo, String directorio) throws IOException {
        WebDriver driver = DriverFactory.obtenerDriver();
        if (driver == null) {
            throw new IllegalStateException("Driver no inicializado");
        }
        
        // Crear directorio si no existe
        new File(directorio).mkdirs();
        
        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File archivoOrigen = takesScreenshot.getScreenshotAs(OutputType.FILE);
        
        String rutaCompleta = directorio + "/" + nombreArchivo + ".png";
        File archivoDestino = new File(rutaCompleta);
        FileUtils.copyFile(archivoOrigen, archivoDestino);
        
        return rutaCompleta;
    }
}

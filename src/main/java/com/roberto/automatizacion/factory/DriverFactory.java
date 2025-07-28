package com.roberto.automatizacion.factory;

import com.roberto.automatizacion.configuracion.ConfiguracionAmbiente;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.time.Duration;

/**
 * Factory para crear instancias de WebDriver
 */
public class DriverFactory {
    
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    public static void inicializarDriver() {
        String navegador = ConfiguracionAmbiente.obtenerNavegador().toLowerCase();
        
        WebDriver webDriver = switch (navegador) {
            case "chrome" -> crearDriverChrome();
            case "firefox" -> crearDriverFirefox();
            case "edge" -> crearDriverEdge();
            default -> {
                System.out.println("Navegador no soportado: " + navegador + ". Usando Chrome por defecto.");
                yield crearDriverChrome();
            }
        };
        
        // Configuraciones comunes
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(
            Duration.ofSeconds(ConfiguracionAmbiente.obtenerTimeoutImplicito()));
        
        driver.set(webDriver);
    }
    
    private static WebDriver crearDriverChrome() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opciones = new ChromeOptions();
        opciones.addArguments("--disable-notifications");
        opciones.addArguments("--disable-popup-blocking");
        opciones.addArguments("--disable-dev-shm-usage");
        opciones.addArguments("--no-sandbox");
        
        // Para ejecución headless (opcional)
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            opciones.addArguments("--headless");
        }
        
        return new ChromeDriver(opciones);
    }
    
    private static WebDriver crearDriverFirefox() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions opciones = new FirefoxOptions();
        
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            opciones.addArguments("--headless");
        }
        
        return new FirefoxDriver(opciones);
    }
    
    private static WebDriver crearDriverEdge() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions opciones = new EdgeOptions();
        
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            opciones.addArguments("--headless");
        }
        
        return new EdgeDriver(opciones);
    }
    
    public static WebDriver obtenerDriver() {
        return driver.get();
    }
    
    public static void cerrarDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            webDriver.quit();
            driver.remove();
        }
    }
}

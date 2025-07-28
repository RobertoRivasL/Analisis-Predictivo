package com.roberto.automatizacion.core;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Gestor robusto de WebDrivers con manejo inteligente de errores y fallbacks
 * Principios aplicados: Singleton, Strategy Pattern, Factory Pattern
 *
 * @author Roberto Rivas Lopez
 */
public final class DriverManager {

    private static final Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> driverLocal = new ThreadLocal<>();
    private static volatile DriverManager instancia;

    // Configuraciones por defecto
    private static final int TIMEOUT_SEGUNDOS = 30;
    private static final int REINTENTOS_MAXIMOS = 3;
    private static final Duration TIEMPO_ESPERA_IMPLICITA = Duration.ofSeconds(10);

    // Rutas de drivers locales como fallback
    private static final String RUTA_DRIVERS_LOCAL = System.getProperty("user.dir") + "/drivers/";

    // Versiones de navegadores soportadas (fallback)
    private static final List<String> VERSIONES_CHROME_SOPORTADAS = Arrays.asList(
            "120", "119", "118", "117", "116", "115"
    );

    private DriverManager() {
        // Constructor privado para Singleton
    }

    /**
     * Obtiene la instancia única del DriverManager (Singleton thread-safe)
     */
    public static DriverManager getInstancia() {
        if (instancia == null) {
            synchronized (DriverManager.class) {
                if (instancia == null) {
                    instancia = new DriverManager();
                    logger.info("DriverManager inicializado correctamente");
                }
            }
        }
        return instancia;
    }

    /**
     * Obtiene el WebDriver actual del hilo
     */
    public WebDriver getDriver() {
        WebDriver driver = driverLocal.get();
        if (driver == null) {
            throw new IllegalStateException("No hay driver inicializado para este hilo. Llama a inicializarDriver() primero.");
        }
        return driver;
    }

    /**
     * Inicializa el WebDriver con manejo robusto de errores
     *
     * @param tipoNavegador Tipo de navegador (chrome, firefox, edge)
     * @param esRemoto Si es ejecución remota
     * @param urlRemota URL del hub remoto (solo si esRemoto = true)
     * @return WebDriver inicializado
     */
    public WebDriver inicializarDriver(TipoNavegador tipoNavegador, boolean esRemoto, String urlRemota) {
        logger.info("Inicializando driver para navegador: {}", tipoNavegador);

        WebDriver driver = null;
        Exception ultimaExcepcion = null;

        // Intentar con reintentos
        for (int intento = 1; intento <= REINTENTOS_MAXIMOS; intento++) {
            try {
                logger.info("Intento {}/{} de inicialización del driver", intento, REINTENTOS_MAXIMOS);

                if (esRemoto) {
                    driver = crearDriverRemoto(tipoNavegador, urlRemota);
                } else {
                    driver = crearDriverLocal(tipoNavegador, intento);
                }

                if (driver != null) {
                    configurarDriver(driver);
                    driverLocal.set(driver);
                    logger.info("Driver {} inicializado exitosamente en intento {}", tipoNavegador, intento);
                    return driver;
                }

            } catch (Exception e) {
                ultimaExcepcion = e;
                logger.warn("Error en intento {}/{}: {}", intento, REINTENTOS_MAXIMOS, e.getMessage());

                if (intento < REINTENTOS_MAXIMOS) {
                    esperarEntreReintentos(intento);
                }
            }
        }

        // Si llegamos aquí, todos los intentos fallaron
        return manejarFallback(tipoNavegador, ultimaExcepcion);
    }

    /**
     * Crea driver local con múltiples estrategias de fallback
     */
    private WebDriver crearDriverLocal(TipoNavegador tipoNavegador, int numeroIntento) {
        try {
            return switch (tipoNavegador) {
                case CHROME -> crearChromeDriver(numeroIntento);
                case FIREFOX -> crearFirefoxDriver(numeroIntento);
                case EDGE -> crearEdgeDriver(numeroIntento);
            };
        } catch (Exception e) {
            logger.error("Error creando driver local para {}: {}", tipoNavegador, e.getMessage());
            throw e;
        }
    }

    /**
     * Crea Chrome Driver con estrategias de fallback
     */
    private WebDriver crearChromeDriver(int numeroIntento) {
        ChromeOptions opciones = obtenerOpcionesChromeRobustas();

        try {
            // Intento 1: WebDriverManager automático
            if (numeroIntento == 1) {
                logger.info("Intentando descargar ChromeDriver automáticamente...");
                WebDriverManager.chromedriver().setup();
                return new ChromeDriver(opciones);
            }

            // Intento 2: Versión específica
            if (numeroIntento == 2) {
                logger.info("Intentando con versión específica de Chrome...");
                String versionChrome = detectarVersionChrome();
                if (versionChrome != null) {
                    WebDriverManager.chromedriver().driverVersion(versionChrome).setup();
                    return new ChromeDriver(opciones);
                }
            }

            // Intento 3: Driver local
            if (numeroIntento == 3) {
                return crearDriverDesdeArchivoLocal("chromedriver.exe", ChromeDriver::new, opciones);
            }

        } catch (Exception e) {
            logger.error("Error en ChromeDriver intento {}: {}", numeroIntento, e.getMessage());
            throw e;
        }

        throw new RuntimeException("No se pudo crear ChromeDriver después de todos los intentos");
    }

    /**
     * Crea Firefox Driver con estrategias de fallback
     */
    private WebDriver crearFirefoxDriver(int numeroIntento) {
        FirefoxOptions opciones = obtenerOpcionesFirefoxRobustas();

        try {
            if (numeroIntento == 1) {
                logger.info("Intentando descargar GeckoDriver automáticamente...");
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver(opciones);
            }

            if (numeroIntento == 2) {
                logger.info("Intentando con detección de versión Firefox...");
                // Firefox usa GeckoDriver, no necesita versión específica del navegador
                WebDriverManager.firefoxdriver().clearDriverCache().setup();
                return new FirefoxDriver(opciones);
            }

            if (numeroIntento == 3) {
                return crearDriverDesdeArchivoLocal("geckodriver.exe",
                        () -> new FirefoxDriver(opciones), opciones);
            }

        } catch (Exception e) {
            logger.error("Error en FirefoxDriver intento {}: {}", numeroIntento, e.getMessage());
            throw e;
        }

        throw new RuntimeException("No se pudo crear FirefoxDriver después de todos los intentos");
    }

    /**
     * Crea Edge Driver con estrategias de fallback
     */
    private WebDriver crearEdgeDriver(int numeroIntento) {
        EdgeOptions opciones = obtenerOpcionesEdgeRobustas();

        try {
            if (numeroIntento == 1) {
                logger.info("Intentando descargar EdgeDriver automáticamente...");
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver(opciones);
            }

            if (numeroIntento == 2) {
                logger.info("Intentando limpiar cache y reintentando EdgeDriver...");
                WebDriverManager.edgedriver().clearDriverCache().clearResolutionCache().setup();
                return new EdgeDriver(opciones);
            }

            if (numeroIntento == 3) {
                return crearDriverDesdeArchivoLocal("msedgedriver.exe",
                        () -> new EdgeDriver(opciones), opciones);
            }

        } catch (Exception e) {
            logger.error("Error en EdgeDriver intento {}: {}", numeroIntento, e.getMessage());
            throw e;
        }

        throw new RuntimeException("No se pudo crear EdgeDriver después de todos los intentos");
    }

    /**
     * Intenta crear driver desde archivo local
     */
    @SuppressWarnings("unchecked")
    private <T extends WebDriver, O> T crearDriverDesdeArchivoLocal(String nombreDriver,
                                                                    java.util.function.Supplier<T> constructor, O opciones) {

        String rutaDriver = RUTA_DRIVERS_LOCAL + nombreDriver;
        File archivoDriver = new File(rutaDriver);

        if (archivoDriver.exists() && archivoDriver.canExecute()) {
            logger.info("Usando driver local: {}", rutaDriver);

            // Establecer property del sistema según el tipo de driver
            if (nombreDriver.contains("chrome")) {
                System.setProperty("webdriver.chrome.driver", rutaDriver);
            } else if (nombreDriver.contains("gecko")) {
                System.setProperty("webdriver.gecko.driver", rutaDriver);
            } else if (nombreDriver.contains("edge")) {
                System.setProperty("webdriver.edge.driver", rutaDriver);
            }

            return constructor.get();
        } else {
            throw new RuntimeException("Driver local no encontrado o no ejecutable: " + rutaDriver);
        }
    }

    /**
     * Detecta la versión de Chrome instalada
     */
    private String detectarVersionChrome() {
        try {
            // En Windows
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                ProcessBuilder pb = new ProcessBuilder("reg", "query",
                        "HKEY_CURRENT_USER\\Software\\Google\\Chrome\\BLBeacon", "/v", "version");
                Process proceso = pb.start();
                // Procesar resultado...
                // Por simplicidad, retornamos null aquí, pero se puede implementar completamente
            }
            return null;
        } catch (Exception e) {
            logger.warn("No se pudo detectar versión de Chrome: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Maneja fallback cuando todos los intentos fallan
     */
    private WebDriver manejarFallback(TipoNavegador tipoOriginal, Exception ultimaExcepcion) {
        logger.error("Todos los intentos fallaron para {}", tipoOriginal);

        // Estrategia de fallback: intentar con otro navegador
        List<TipoNavegador> navegadoresFallback = Arrays.asList(
                TipoNavegador.CHROME, TipoNavegador.FIREFOX, TipoNavegador.EDGE
        );

        for (TipoNavegador navegadorFallback : navegadoresFallback) {
            if (navegadorFallback != tipoOriginal) {
                try {
                    logger.info("Intentando fallback con navegador: {}", navegadorFallback);
                    WebDriver driverFallback = crearDriverLocal(navegadorFallback, 1);
                    if (driverFallback != null) {
                        configurarDriver(driverFallback);
                        driverLocal.set(driverFallback);
                        logger.warn("Fallback exitoso: usando {} en lugar de {}",
                                navegadorFallback, tipoOriginal);
                        return driverFallback;
                    }
                } catch (Exception e) {
                    logger.warn("Fallback con {} también falló: {}", navegadorFallback, e.getMessage());
                }
            }
        }

        // Si llegamos aquí, no hay solución
        throw new RuntimeException("No se pudo inicializar ningún navegador. Última excepción: " +
                ultimaExcepcion.getMessage(), ultimaExcepcion);
    }

    /**
     * Configura el driver con timeouts y configuraciones comunes
     */
    private void configurarDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(TIEMPO_ESPERA_IMPLICITA);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TIMEOUT_SEGUNDOS));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(TIMEOUT_SEGUNDOS));
        driver.manage().window().maximize();

        logger.info("Driver configurado con timeouts y ventana maximizada");
    }

    /**
     * Opciones robustas para Chrome
     */
    private ChromeOptions obtenerOpcionesChromeRobustas() {
        ChromeOptions opciones = new ChromeOptions();
        opciones.addArguments("--no-sandbox");
        opciones.addArguments("--disable-dev-shm-usage");
        opciones.addArguments("--disable-gpu");
        opciones.addArguments("--disable-extensions");
        opciones.addArguments("--disable-web-security");
        opciones.addArguments("--allow-running-insecure-content");
        opciones.addArguments("--ignore-certificate-errors");
        opciones.addArguments("--ignore-ssl-errors");
        opciones.addArguments("--ignore-certificate-errors-spki-list");

        // Para ejecución en contenedores
        opciones.addArguments("--disable-background-timer-throttling");
        opciones.addArguments("--disable-renderer-backgrounding");
        opciones.addArguments("--disable-backgrounding-occluded-windows");

        return opciones;
    }

    /**
     * Opciones robustas para Firefox
     */
    private FirefoxOptions obtenerOpcionesFirefoxRobustas() {
        FirefoxOptions opciones = new FirefoxOptions();
        opciones.addArguments("--no-sandbox");
        opciones.addArguments("--disable-dev-shm-usage");
        opciones.addPreference("geo.enabled", false);
        opciones.addPreference("geo.provider.use_corelocation", false);
        opciones.addPreference("geo.prompt.testing", false);
        opciones.addPreference("geo.prompt.testing.always_deny", true);

        return opciones;
    }

    /**
     * Opciones robustas para Edge
     */
    private EdgeOptions obtenerOpcionesEdgeRobustas() {
        EdgeOptions opciones = new EdgeOptions();
        opciones.addArguments("--no-sandbox");
        opciones.addArguments("--disable-dev-shm-usage");
        opciones.addArguments("--disable-gpu");
        opciones.addArguments("--disable-extensions");

        return opciones;
    }

    /**
     * Crea driver remoto para ejecución distribuida
     */
    private WebDriver crearDriverRemoto(TipoNavegador tipoNavegador, String urlRemota) {
        try {
            URL url = new URL(urlRemota);

            return switch (tipoNavegador) {
                case CHROME -> new RemoteWebDriver(url, obtenerOpcionesChromeRobustas());
                case FIREFOX -> new RemoteWebDriver(url, obtenerOpcionesFirefoxRobustas());
                case EDGE -> new RemoteWebDriver(url, obtenerOpcionesEdgeRobustas());
            };

        } catch (Exception e) {
            throw new RuntimeException("Error creando driver remoto: " + e.getMessage(), e);
        }
    }

    /**
     * Espera entre reintentos con backoff exponencial
     */
    private void esperarEntreReintentos(int numeroIntento) {
        try {
            long tiempoEspera = (long) (1000 * Math.pow(2, numeroIntento - 1)); // 1s, 2s, 4s...
            logger.info("Esperando {}ms antes del siguiente intento...", tiempoEspera);
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrumpido durante espera entre reintentos", e);
        }
    }

    /**
     * Cierra el driver actual del hilo
     */
    public void cerrarDriver() {
        WebDriver driver = driverLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                logger.info("Driver cerrado correctamente");
            } catch (Exception e) {
                logger.error("Error cerrando driver: {}", e.getMessage());
            } finally {
                driverLocal.remove();
            }
        }
    }

    /**
     * Verifica si hay un driver activo
     */
    public boolean hayDriverActivo() {
        return driverLocal.get() != null;
    }

    /**
     * Método de conveniencia para inicialización simple
     */
    public WebDriver inicializarDriver(TipoNavegador tipoNavegador) {
        return inicializarDriver(tipoNavegador, false, null);
    }

    /**
     * Método de conveniencia para inicialización con string
     */
    public WebDriver inicializarDriver(String nombreNavegador) {
        TipoNavegador tipo = TipoNavegador.fromString(nombreNavegador);
        return inicializarDriver(tipo, false, null);
    }

    /**
     * Enum para tipos de navegador soportados
     */
    public enum TipoNavegador {
        CHROME("chrome"),
        FIREFOX("firefox"),
        EDGE("edge");

        private final String nombre;

        TipoNavegador(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        public static TipoNavegador fromString(String nombreNavegador) {
            if (nombreNavegador == null) {
                throw new IllegalArgumentException("Nombre de navegador no puede ser null");
            }

            return switch (nombreNavegador.toLowerCase().trim()) {
                case "chrome", "google chrome", "googlechrome" -> CHROME;
                case "firefox", "mozilla firefox", "mozillafirefox", "ff" -> FIREFOX;
                case "edge", "microsoft edge", "microsoftedge", "msedge" -> EDGE;
                default -> throw new IllegalArgumentException("Navegador no soportado: " + nombreNavegador);
            };
        }
    }
}
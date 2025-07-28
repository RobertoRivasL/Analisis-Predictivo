package com.roberto.automatizacion.base;

import com.roberto.automatizacion.config.ConfigManager;
import com.roberto.automatizacion.core.DriverManager;
import com.roberto.automatizacion.paginas.FabricaPaginas;
import com.roberto.automatizacion.utilidades.UtilidadesScreenshot;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase base para todas las clases de prueba
 * Implementa patrones: Template Method, Strategy, Factory
 * Principios SOLID aplicados: SRP, OCP, LSP, ISP, DIP
 *
 * @author Roberto Rivas Lopez
 */
public abstract class PruebaBase {

    protected static final Logger logger = LoggerFactory.getLogger(PruebaBase.class);

    // Gestores y configuración
    protected ConfigManager configuracion;
    protected DriverManager gestorDriver;
    protected FabricaPaginas fabricaPaginas;

    // Métricas de prueba
    private long tiempoInicioPrueba;
    private String nombrePruebaActual;

    // Configuraciones de timeout por defecto
    protected static final Duration TIMEOUT_PRUEBA_CORTA = Duration.ofSeconds(30);
    protected static final Duration TIMEOUT_PRUEBA_MEDIA = Duration.ofMinutes(2);
    protected static final Duration TIMEOUT_PRUEBA_LARGA = Duration.ofMinutes(5);

    // ========================================
    // CONFIGURACIÓN DE SUITE (EJECUTA UNA VEZ)
    // ========================================

    /**
     * Configuración inicial de la suite de pruebas
     * Se ejecuta una sola vez antes de todas las pruebas
     */
    @BeforeSuite(alwaysRun = true)
    public void configurarSuite() {
        logger.info("=== INICIANDO SUITE DE PRUEBAS ===");

        try {
            // Inicializar configuración
            configuracion = ConfigManager.getInstancia();
            logger.info("Configuración cargada - Navegador: {}, Entorno: {}",
                    configuracion.obtenerNavegador(), configuracion.obtenerEntorno());

            // Crear directorios necesarios
            crearDirectoriosReporte();

            // Configuraciones adicionales de suite
            configuracionesAdicionalesSuite();

            logger.info("Suite configurada exitosamente");

        } catch (Exception e) {
            logger.error("Error configurando suite: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en configuración de suite", e);
        }
    }

    /**
     * Limpieza final de la suite
     * Se ejecuta una sola vez después de todas las pruebas
     */
    @AfterSuite(alwaysRun = true)
    public void limpiarSuite() {
        logger.info("=== FINALIZANDO SUITE DE PRUEBAS ===");

        try {
            // Limpiar cache de páginas
            if (fabricaPaginas != null) {
                fabricaPaginas.limpiarCache();
            }

            // Generar reportes finales
            generarReportesSuite();

            // Limpieza adicional de suite
            limpiezaAdicionalSuite();

            logger.info("Suite finalizada exitosamente");

        } catch (Exception e) {
            logger.error("Error en limpieza de suite: {}", e.getMessage(), e);
        }
    }

    // ========================================
    // CONFIGURACIÓN DE CLASE (POR CLASE DE PRUEBA)
    // ========================================

    /**
     * Configuración por clase de prueba
     * Se ejecuta una vez antes de cada clase de prueba
     */
    @BeforeClass(alwaysRun = true)
    public void configurarClasePrueba() {
        String nombreClase = this.getClass().getSimpleName();
        logger.info("=== INICIANDO CLASE DE PRUEBA: {} ===", nombreClase);

        try {
            // Configuraciones específicas por clase
            configuracionesEspecificasClase();

            logger.info("Clase {} configurada exitosamente", nombreClase);

        } catch (Exception e) {
            logger.error("Error configurando clase {}: {}", nombreClase, e.getMessage(), e);
            throw new RuntimeException("Fallo en configuración de clase", e);
        }
    }

    /**
     * Limpieza por clase de prueba
     */
    @AfterClass(alwaysRun = true)
    public void limpiarClasePrueba() {
        String nombreClase = this.getClass().getSimpleName();
        logger.info("=== FINALIZANDO CLASE DE PRUEBA: {} ===", nombreClase);

        try {
            // Limpieza específica por clase
            limpiezaEspecificaClase();

            logger.info("Clase {} finalizada exitosamente", nombreClase);

        } catch (Exception e) {
            logger.error("Error en limpieza de clase {}: {}", nombreClase, e.getMessage(), e);
        }
    }

    // ========================================
    // CONFIGURACIÓN DE MÉTODO (POR PRUEBA)
    // ========================================

    /**
     * Configuración antes de cada método de prueba
     */
    @BeforeMethod(alwaysRun = true)
    public void configurarPrueba(Method metodo) {
        nombrePruebaActual = metodo.getName();
        tiempoInicioPrueba = System.currentTimeMillis();

        logger.info("🧪 INICIANDO PRUEBA: {}", nombrePruebaActual);

        try {
            // Inicializar WebDriver
            inicializarDriver();

            // Inicializar PageFactory
            inicializarFabricaPaginas();

            // Configuraciones pre-prueba
            configuracionesPrePrueba(metodo);

            logger.info("Prueba {} configurada exitosamente", nombrePruebaActual);

        } catch (Exception e) {
            logger.error("Error configurando prueba {}: {}", nombrePruebaActual, e.getMessage(), e);
            throw new RuntimeException("Fallo en configuración de prueba", e);
        }
    }

    /**
     * Limpieza después de cada método de prueba
     */
    @AfterMethod(alwaysRun = true)
    public void limpiarPrueba(ITestResult resultado) {
        long tiempoEjecucion = System.currentTimeMillis() - tiempoInicioPrueba;

        try {
            // Procesar resultado de la prueba
            procesarResultadoPrueba(resultado, tiempoEjecucion);

            // Limpieza post-prueba
            limpiezaPostPrueba(resultado);

            // Cerrar WebDriver
            cerrarDriver();

            logger.info("🏁 FINALIZANDO PRUEBA: {} - Tiempo: {}ms - Estado: {}",
                    nombrePruebaActual, tiempoEjecucion, obtenerEstadoPrueba(resultado));

        } catch (Exception e) {
            logger.error("Error en limpieza de prueba {}: {}", nombrePruebaActual, e.getMessage(), e);
        }
    }

    // ========================================
    // MÉTODOS DE INICIALIZACIÓN
    // ========================================

    /**
     * Inicializa el WebDriver según la configuración
     */
    private void inicializarDriver() {
        try {
            gestorDriver = DriverManager.getInstancia();

            String navegador = configuracion.obtenerNavegador();
            boolean esRemoto = configuracion.esEjecucionRemota();
            String urlRemota = configuracion.obtenerUrlRemota();

            DriverManager.TipoNavegador tipoNavegador = DriverManager.TipoNavegador.fromString(navegador);
            gestorDriver.inicializarDriver(tipoNavegador, esRemoto, urlRemota);

            logger.debug("Driver inicializado: {} (Remoto: {})", navegador, esRemoto);

        } catch (Exception e) {
            logger.error("Error inicializando driver: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar WebDriver", e);
        }
    }

    /**
     * Inicializa la fábrica de páginas
     */
    private void inicializarFabricaPaginas() {
        try {
            fabricaPaginas = FabricaPaginas.getInstancia();
            logger.debug("Fábrica de páginas inicializada");

        } catch (Exception e) {
            logger.error("Error inicializando fábrica de páginas: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar fábrica de páginas", e);
        }
    }

    /**
     * Cierra el WebDriver
     */
    private void cerrarDriver() {
        try {
            if (gestorDriver != null && gestorDriver.hayDriverActivo()) {
                gestorDriver.cerrarDriver();
                logger.debug("Driver cerrado correctamente");
            }
        } catch (Exception e) {
            logger.warn("Error cerrando driver: {}", e.getMessage());
        }
    }

    // ========================================
    // PROCESAMIENTO DE RESULTADOS
    // ========================================

    /**
     * Procesa el resultado de la prueba ejecutada
     */
    private void procesarResultadoPrueba(ITestResult resultado, long tiempoEjecucion) {
        String estadoPrueba = obtenerEstadoPrueba(resultado);

        switch (resultado.getStatus()) {
            case ITestResult.SUCCESS -> {
                logger.info("✅ ÉXITO: {} ({}ms)", nombrePruebaActual, tiempoEjecucion);
                procesarPruebaExitosa(resultado);
            }
            case ITestResult.FAILURE -> {
                logger.error("❌ FALLO: {} ({}ms) - {}",
                        nombrePruebaActual, tiempoEjecucion, resultado.getThrowable().getMessage());
                procesarPruebaFallida(resultado);
            }
            case ITestResult.SKIP -> {
                logger.warn("⏭️ OMITIDA: {} - {}",
                        nombrePruebaActual,
                        resultado.getThrowable() != null ? resultado.getThrowable().getMessage() : "Razón no especificada");
                procesarPruebaOmitida(resultado);
            }
            default -> {
                logger.warn("Estado desconocido para prueba: {}", nombrePruebaActual);
            }
        }
    }

    /**
     * Procesa una prueba exitosa
     */
    private void procesarPruebaExitosa(ITestResult resultado) {
        try {
            // Screenshot opcional para pruebas exitosas (si está configurado)
            if (Boolean.parseBoolean(System.getProperty("screenshot.success", "false"))) {
                tomarScreenshotPrueba("EXITO");
            }

            // Métricas adicionales para pruebas exitosas
            registrarMetricasExito(resultado);

        } catch (Exception e) {
            logger.warn("Error procesando prueba exitosa: {}", e.getMessage());
        }
    }

    /**
     * Procesa una prueba fallida
     */
    private void procesarPruebaFallida(ITestResult resultado) {
        try {
            // Screenshot obligatorio para pruebas fallidas
            String rutaScreenshot = tomarScreenshotPrueba("FALLO");

            // Adjuntar screenshot a Allure
            if (rutaScreenshot != null) {
                adjuntarScreenshotAllure(rutaScreenshot);
            }

            // Log del stack trace completo
            if (resultado.getThrowable() != null) {
                logger.error("Stack trace completo:", resultado.getThrowable());
            }

            // Información adicional de debugging
            registrarInformacionDebug();

            // Métricas para pruebas fallidas
            registrarMetricasFallo(resultado);

        } catch (Exception e) {
            logger.error("Error procesando prueba fallida: {}", e.getMessage());
        }
    }

    /**
     * Procesa una prueba omitida
     */
    private void procesarPruebaOmitida(ITestResult resultado) {
        try {
            // Registrar razón de omisión
            String razonOmision = resultado.getThrowable() != null ?
                    resultado.getThrowable().getMessage() : "Razón no especificada";

            logger.info("Razón de omisión: {}", razonOmision);

            // Métricas para pruebas omitidas
            registrarMetricasOmision(resultado);

        } catch (Exception e) {
            logger.warn("Error procesando prueba omitida: {}", e.getMessage());
        }
    }

    // ========================================
    // UTILIDADES DE SCREENSHOT
    // ========================================

    /**
     * Toma screenshot de la prueba actual
     */
    private String tomarScreenshotPrueba(String sufijo) {
        try {
            String nombreArchivo = nombrePruebaActual + "_" + sufijo;
            return UtilidadesScreenshot.tomarScreenshot(nombreArchivo);

        } catch (IOException e) {
            logger.error("Error tomando screenshot: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Adjunta screenshot a Allure
     */
    @Attachment(value = "Screenshot", type = "image/png")
    private byte[] adjuntarScreenshotAllure(String rutaScreenshot) {
        try {
            if (gestorDriver != null && gestorDriver.hayDriverActivo()) {
                TakesScreenshot screenshot = (TakesScreenshot) gestorDriver.getDriver();
                return screenshot.getScreenshotAs(OutputType.BYTES);
            }
        } catch (Exception e) {
            logger.error("Error adjuntando screenshot a Allure: {}", e.getMessage());
        }
        return new byte[0];
    }

    // ========================================
    // UTILIDADES DE INFORMACIÓN Y MÉTRICAS
    // ========================================

    /**
     * Obtiene el estado de la prueba como string
     */
    private String obtenerEstadoPrueba(ITestResult resultado) {
        return switch (resultado.getStatus()) {
            case ITestResult.SUCCESS -> "ÉXITO";
            case ITestResult.FAILURE -> "FALLO";
            case ITestResult.SKIP -> "OMITIDA";
            default -> "DESCONOCIDO";
        };
    }

    /**
     * Registra información de debugging adicional
     */
    private void registrarInformacionDebug() {
        try {
            if (gestorDriver != null && gestorDriver.hayDriverActivo()) {
                String urlActual = gestorDriver.getDriver().getCurrentUrl();
                String titulo = gestorDriver.getDriver().getTitle();

                logger.info("=== INFORMACIÓN DE DEBUG ===");
                logger.info("URL actual: {}", urlActual);
                logger.info("Título página: {}", titulo);
                logger.info("Timestamp: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        } catch (Exception e) {
            logger.warn("Error registrando información de debug: {}", e.getMessage());
        }
    }

    // ========================================
    // MÉTODOS DE MÉTRICAS (PARA EXTENSIÓN FUTURA)
    // ========================================

    /**
     * Registra métricas para pruebas exitosas
     */
    protected void registrarMetricasExito(ITestResult resultado) {
        // Implementación futura para métricas detalladas
        logger.debug("Registrando métricas de éxito para: {}", nombrePruebaActual);
    }

    /**
     * Registra métricas para pruebas fallidas
     */
    protected void registrarMetricasFallo(ITestResult resultado) {
        // Implementación futura para análisis de fallos
        logger.debug("Registrando métricas de fallo para: {}", nombrePruebaActual);
    }

    /**
     * Registra métricas para pruebas omitidas
     */
    protected void registrarMetricasOmision(ITestResult resultado) {
        // Implementación futura para análisis de omisiones
        logger.debug("Registrando métricas de omisión para: {}", nombrePruebaActual);
    }

    // ========================================
    // MÉTODOS ABSTRACTOS Y PLANTILLAS (TEMPLATE METHOD)
    // ========================================

    /**
     * Configuraciones adicionales específicas de suite
     * Las clases hijas pueden sobrescribir este método
     */
    protected void configuracionesAdicionalesSuite() {
        // Implementación por defecto vacía
        logger.debug("Ejecutando configuraciones adicionales de suite (por defecto)");
    }

    /**
     * Limpieza adicional específica de suite
     */
    protected void limpiezaAdicionalSuite() {
        // Implementación por defecto vacía
        logger.debug("Ejecutando limpieza adicional de suite (por defecto)");
    }

    /**
     * Configuraciones específicas por clase
     */
    protected void configuracionesEspecificasClase() {
        // Implementación por defecto vacía
        logger.debug("Ejecutando configuraciones específicas de clase (por defecto)");
    }

    /**
     * Limpieza específica por clase
     */
    protected void limpiezaEspecificaClase() {
        // Implementación por defecto vacía
        logger.debug("Ejecutando limpieza específica de clase (por defecto)");
    }

    /**
     * Configuraciones antes de cada prueba
     */
    protected void configuracionesPrePrueba(Method metodo) {
        // Implementación por defecto vacía
        logger.debug("Ejecutando configuraciones pre-prueba (por defecto) para: {}", metodo.getName());
    }

    /**
     * Limpieza después de cada prueba
     */
    protected void limpiezaPostPrueba(ITestResult resultado) {
        // Implementación por defecto vacía
        logger.debug("Ejecutando limpieza post-prueba (por defecto)");
    }

    // ========================================
    // UTILIDADES PRIVADAS
    // ========================================

    /**
     * Crea los directorios necesarios para reportes
     */
    private void crearDirectoriosReporte() {
        try {
            java.io.File directorioReportes = new java.io.File("reportes");
            if (!directorioReportes.exists()) {
                directorioReportes.mkdirs();
            }

            java.io.File directorioScreenshots = new java.io.File("reportes/screenshots");
            if (!directorioScreenshots.exists()) {
                directorioScreenshots.mkdirs();
            }

            logger.debug("Directorios de reporte creados");

        } catch (Exception e) {
            logger.warn("Error creando directorios de reporte: {}", e.getMessage());
        }
    }

    /**
     * Genera reportes finales de suite
     */
    private void generarReportesSuite() {
        try {
            // Implementación futura para generación de reportes
            logger.info("Generando reportes finales de suite...");

            // Aquí se pueden integrar reportes personalizados, métricas, etc.

        } catch (Exception e) {
            logger.error("Error generando reportes de suite: {}", e.getMessage());
        }
    }

    // ========================================
    // GETTERS PROTEGIDOS PARA CLASES HIJAS
    // ========================================

    /**
     * Obtiene la configuración actual
     */
    protected ConfigManager obtenerConfiguracion() {
        return configuracion;
    }

    /**
     * Obtiene el gestor de driver actual
     */
    protected DriverManager obtenerGestorDriver() {
        return gestorDriver;
    }

    /**
     * Obtiene la fábrica de páginas actual
     */
    protected FabricaPaginas obtenerFabricaPaginas() {
        return fabricaPaginas;
    }

    /**
     * Obtiene el nombre de la prueba actual
     */
    protected String obtenerNombrePruebaActual() {
        return nombrePruebaActual;
    }
}
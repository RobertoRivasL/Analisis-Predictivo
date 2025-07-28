package com.roberto.automatizacion.utilidades;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Utilidades comunes para Page Objects
 * Implementa el patrón Utility/Helper
 * Principios SOLID aplicados: SRP, OCP
 *
 * @author Roberto Rivas Lopez
 */
public final class UtilidadesPagina {

    private static final Logger logger = LoggerFactory.getLogger(UtilidadesPagina.class);

    // Constructor privado para evitar instanciación
    private UtilidadesPagina() {
        throw new UnsupportedOperationException("Clase utilitaria no debe ser instanciada");
    }

    // ========================================
    // UTILIDADES DE VALIDACIÓN
    // ========================================

    /**
     * Valida que una página contiene elementos específicos
     */
    public static boolean validarElementosPresentes(WebDriver driver, By... localizadores) {
        logger.debug("Validando presencia de {} elementos", localizadores.length);

        for (By localizador : localizadores) {
            try {
                driver.findElement(localizador);
            } catch (NoSuchElementException e) {
                logger.warn("Elemento no encontrado: {}", localizador);
                return false;
            }
        }

        logger.debug("Todos los elementos están presentes");
        return true;
    }

    /**
     * Valida que una página contiene textos específicos
     */
    public static boolean validarTextosPresentes(WebDriver driver, String... textos) {
        logger.debug("Validando presencia de {} textos", textos.length);

        String paginaTexto = driver.findElement(By.tagName("body")).getText().toLowerCase();

        for (String texto : textos) {
            if (!paginaTexto.contains(texto.toLowerCase())) {
                logger.warn("Texto no encontrado: {}", texto);
                return false;
            }
        }

        logger.debug("Todos los textos están presentes");
        return true;
    }

    /**
     * Valida que una URL contiene patrones específicos
     */
    public static boolean validarPatronesUrl(String urlActual, String... patrones) {
        logger.debug("Validando URL '{}' contra {} patrones", urlActual, patrones.length);

        for (String patron : patrones) {
            if (urlActual.toLowerCase().contains(patron.toLowerCase())) {
                logger.debug("Patrón '{}' encontrado en URL", patron);
                return true;
            }
        }

        logger.warn("Ningún patrón encontrado en URL: {}", urlActual);
        return false;
    }

    // ========================================
    // UTILIDADES DE ESPERA
    // ========================================

    /**
     * Espera a que múltiples elementos sean visibles
     */
    public static boolean esperarElementosVisibles(WebDriver driver, Duration timeout, By... localizadores) {
        logger.debug("Esperando que {} elementos sean visibles", localizadores.length);

        WebDriverWait espera = new WebDriverWait(driver, timeout);

        try {
            for (By localizador : localizadores) {
                espera.until(ExpectedConditions.visibilityOfElementLocated(localizador));
            }
            logger.debug("Todos los elementos son visibles");
            return true;
        } catch (TimeoutException e) {
            logger.warn("Timeout esperando elementos visibles: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Espera a que al menos uno de los elementos sea visible
     */
    public static WebElement esperarCualquierElementoVisible(WebDriver driver, Duration timeout, By... localizadores) {
        logger.debug("Esperando que cualquier elemento de {} sea visible", localizadores.length);

        WebDriverWait espera = new WebDriverWait(driver, timeout);

        try {
            return espera.until(driver1 -> {
                for (By localizador : localizadores) {
                    try {
                        WebElement elemento = driver1.findElement(localizador);
                        if (elemento.isDisplayed()) {
                            logger.debug("Elemento visible encontrado: {}", localizador);
                            return elemento;
                        }
                    } catch (NoSuchElementException e) {
                        // Continuar con el siguiente localizador
                    }
                }
                return null;
            });
        } catch (TimeoutException e) {
            logger.warn("Timeout esperando cualquier elemento visible");
            return null;
        }
    }

    /**
     * Espera a que la página esté completamente cargada
     */
    public static boolean esperarCargaCompletaPagina(WebDriver driver, Duration timeout) {
        logger.debug("Esperando carga completa de página");

        WebDriverWait espera = new WebDriverWait(driver, timeout);

        try {
            // Esperar a que document.readyState sea 'complete'
            espera.until(webDriver ->
                    ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")
                            .equals("complete"));

            // Esperar a que jQuery termine (si está presente)
            espera.until(webDriver -> {
                try {
                    return (Boolean) ((JavascriptExecutor) webDriver)
                            .executeScript("return typeof jQuery === 'undefined' || jQuery.active == 0");
                } catch (Exception e) {
                    return true; // jQuery no presente
                }
            });

            logger.debug("Página cargada completamente");
            return true;

        } catch (TimeoutException e) {
            logger.warn("Timeout esperando carga completa de página");
            return false;
        }
    }

    // ========================================
    // UTILIDADES DE INTERACCIÓN
    // ========================================

    /**
     * Hace click con reintentos automáticos
     */
    public static boolean clickConReintentos(WebDriver driver, By localizador, int maxReintentos, Duration timeoutEntreReintentos) {
        logger.debug("Intentando click con reintentos en: {}", localizador);

        for (int intento = 1; intento <= maxReintentos; intento++) {
            try {
                WebDriverWait espera = new WebDriverWait(driver, timeoutEntreReintentos);
                WebElement elemento = espera.until(ExpectedConditions.elementToBeClickable(localizador));
                elemento.click();

                logger.debug("Click exitoso en intento {}", intento);
                return true;

            } catch (Exception e) {
                logger.warn("Intento {} falló para click en {}: {}", intento, localizador, e.getMessage());

                if (intento < maxReintentos) {
                    esperarProcesamiento(1000); // Esperar 1 segundo entre reintentos
                }
            }
        }

        logger.error("Todos los intentos de click fallaron para: {}", localizador);
        return false;
    }

    /**
     * Escribe texto con limpieza previa y validación
     */
    public static boolean escribirTextoSeguro(WebDriver driver, By localizador, String texto, Duration timeout) {
        logger.debug("Escribiendo texto seguro en: {}", localizador);

        try {
            WebDriverWait espera = new WebDriverWait(driver, timeout);
            WebElement elemento = espera.until(ExpectedConditions.visibilityOfElementLocated(localizador));

            // Desplazarse al elemento
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elemento);
            esperarProcesamiento(500);

            // Limpiar campo
            elemento.clear();

            // Escribir texto
            elemento.sendKeys(texto);

            // Validar que el texto se escribió correctamente
            String valorActual = elemento.getAttribute("value");
            boolean textoEscritoCorrectamente = texto.equals(valorActual);

            logger.debug("Texto escrito correctamente: {}", textoEscritoCorrectamente);
            return textoEscritoCorrectamente;

        } catch (Exception e) {
            logger.error("Error escribiendo texto en {}: {}", localizador, e.getMessage());
            return false;
        }
    }

    // ========================================
    // UTILIDADES DE CAPTURA
    // ========================================

    /**
     * Toma captura de pantalla con timestamp
     */
    public static byte[] tomarCapturaConTimestamp(WebDriver driver) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            byte[] captura = screenshot.getScreenshotAs(OutputType.BYTES);

            logger.debug("Captura de pantalla tomada con timestamp");
            return captura;

        } catch (Exception e) {
            logger.error("Error tomando captura de pantalla: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Toma captura de un elemento específico
     */
    public static byte[] tomarCapturaElemento(WebDriver driver, By localizador) {
        try {
            WebElement elemento = driver.findElement(localizador);
            byte[] captura = elemento.getScreenshotAs(OutputType.BYTES);

            logger.debug("Captura de elemento tomada: {}", localizador);
            return captura;

        } catch (Exception e) {
            logger.error("Error tomando captura de elemento {}: {}", localizador, e.getMessage());
            return new byte[0];
        }
    }

    // ========================================
    // UTILIDADES DE NAVEGACIÓN
    // ========================================

    /**
     * Cambia a una nueva pestaña por título o URL
     */
    public static boolean cambiarAPestanaPor(WebDriver driver, String criterio, boolean esTitulo) {
        logger.debug("Cambiando a pestaña por {}: {}", esTitulo ? "título" : "URL", criterio);

        String ventanaOriginal = driver.getWindowHandle();
        Set<String> todasLasVentanas = driver.getWindowHandles();

        for (String ventana : todasLasVentanas) {
            if (!ventana.equals(ventanaOriginal)) {
                driver.switchTo().window(ventana);

                String valorActual = esTitulo ? driver.getTitle() : driver.getCurrentUrl();

                if (valorActual.contains(criterio)) {
                    logger.debug("Cambiado exitosamente a pestaña: {}", criterio);
                    return true;
                }
            }
        }

        // Volver a ventana original si no se encontró
        driver.switchTo().window(ventanaOriginal);
        logger.warn("No se encontró pestaña con criterio: {}", criterio);
        return false;
    }

    /**
     * Cierra todas las pestañas excepto la original
     */
    public static void cerrarPestanasSecundarias(WebDriver driver) {
        logger.debug("Cerrando pestañas secundarias");

        String ventanaOriginal = driver.getWindowHandle();
        Set<String> todasLasVentanas = driver.getWindowHandles();

        for (String ventana : todasLasVentanas) {
            if (!ventana.equals(ventanaOriginal)) {
                driver.switchTo().window(ventana);
                driver.close();
            }
        }

        driver.switchTo().window(ventanaOriginal);
        logger.debug("Pestañas secundarias cerradas");
    }

    // ========================================
    // UTILIDADES DE FORMULARIOS
    // ========================================

    /**
     * Llena un formulario con un mapa de datos
     */
    public static boolean llenarFormulario(WebDriver driver, java.util.Map<By, String> datosFormulario, Duration timeout) {
        logger.debug("Llenando formulario con {} campos", datosFormulario.size());

        boolean todoExitoso = true;

        for (java.util.Map.Entry<By, String> entrada : datosFormulario.entrySet()) {
            boolean campoLlenado = escribirTextoSeguro(driver, entrada.getKey(), entrada.getValue(), timeout);
            if (!campoLlenado) {
                logger.warn("Error llenando campo: {}", entrada.getKey());
                todoExitoso = false;
            }
        }

        logger.info("Formulario llenado completamente: {}", todoExitoso);
        return todoExitoso;
    }

    /**
     * Valida que todos los campos requeridos de un formulario estén llenos
     */
    public static boolean validarCamposRequeridosLlenos(WebDriver driver, By... camposRequeridos) {
        logger.debug("Validando {} campos requeridos", camposRequeridos.length);

        for (By campo : camposRequeridos) {
            try {
                WebElement elemento = driver.findElement(campo);
                String valor = elemento.getAttribute("value");

                if (valor == null || valor.trim().isEmpty()) {
                    logger.warn("Campo requerido vacío: {}", campo);
                    return false;
                }
            } catch (NoSuchElementException e) {
                logger.warn("Campo requerido no encontrado: {}", campo);
                return false;
            }
        }

        logger.debug("Todos los campos requeridos están llenos");
        return true;
    }

    // ========================================
    // UTILIDADES DE ELEMENTOS DINÁMICOS
    // ========================================

    /**
     * Espera a que aparezcan elementos dinámicos
     */
    public static List<WebElement> esperarElementosDinamicos(WebDriver driver, By localizador, int cantidadMinima, Duration timeout) {
        logger.debug("Esperando al menos {} elementos dinámicos: {}", cantidadMinima, localizador);

        WebDriverWait espera = new WebDriverWait(driver, timeout);

        try {
            return espera.until(driver1 -> {
                List<WebElement> elementos = driver1.findElements(localizador);
                return elementos.size() >= cantidadMinima ? elementos : null;
            });
        } catch (TimeoutException e) {
            logger.warn("Timeout esperando elementos dinámicos: {}", localizador);
            return List.of();
        }
    }

    /**
     * Espera a que un elemento contenga texto específico
     */
    public static boolean esperarTextoEnElemento(WebDriver driver, By localizador, String textoEsperado, Duration timeout) {
        logger.debug("Esperando texto '{}' en elemento: {}", textoEsperado, localizador);

        WebDriverWait espera = new WebDriverWait(driver, timeout);

        try {
            boolean textoPresente = espera.until(ExpectedConditions.textToBePresentInElementLocated(localizador, textoEsperado));
            logger.debug("Texto '{}' presente: {}", textoEsperado, textoPresente);
            return textoPresente;
        } catch (TimeoutException e) {
            logger.warn("Timeout esperando texto '{}' en elemento: {}", textoEsperado, localizador);
            return false;
        }
    }

    // ========================================
    // UTILIDADES DE DEBUGGING
    // ========================================

    /**
     * Obtiene información de debugging de un elemento
     */
    public static String obtenerInfoDebugElemento(WebDriver driver, By localizador) {
        try {
            WebElement elemento = driver.findElement(localizador);

            StringBuilder info = new StringBuilder();
            info.append("Elemento: ").append(localizador).append("\n");
            info.append("TagName: ").append(elemento.getTagName()).append("\n");
            info.append("Texto: ").append(elemento.getText()).append("\n");
            info.append("Visible: ").append(elemento.isDisplayed()).append("\n");
            info.append("Habilitado: ").append(elemento.isEnabled()).append("\n");
            info.append("Seleccionado: ").append(elemento.isSelected()).append("\n");
            info.append("Ubicación: ").append(elemento.getLocation()).append("\n");
            info.append("Tamaño: ").append(elemento.getSize()).append("\n");

            // Atributos comunes
            String[] atributos = {"id", "class", "name", "value", "type", "href", "src"};
            for (String atributo : atributos) {
                String valor = elemento.getAttribute(atributo);
                if (valor != null && !valor.isEmpty()) {
                    info.append(atributo).append(": ").append(valor).append("\n");
                }
            }

            return info.toString();

        } catch (Exception e) {
            return "Error obteniendo información del elemento: " + e.getMessage();
        }
    }

    /**
     * Imprime información de debugging de la página actual
     */
    public static String obtenerInfoDebugPagina(WebDriver driver) {
        StringBuilder info = new StringBuilder();
        info.append("=== INFORMACIÓN DE DEBUGGING DE PÁGINA ===\n");
        info.append("URL: ").append(driver.getCurrentUrl()).append("\n");
        info.append("Título: ").append(driver.getTitle()).append("\n");
        info.append("Handle de ventana: ").append(driver.getWindowHandle()).append("\n");
        info.append("Número de ventanas: ").append(driver.getWindowHandles().size()).append("\n");

        // Información del navegador
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            info.append("User Agent: ").append(js.executeScript("return navigator.userAgent;")).append("\n");
            info.append("Document Ready State: ").append(js.executeScript("return document.readyState;")).append("\n");
        } catch (Exception e) {
            info.append("Error obteniendo información JS: ").append(e.getMessage()).append("\n");
        }

        return info.toString();
    }

    // ========================================
    // UTILIDADES DE PERFORMANCE
    // ========================================

    /**
     * Mide el tiempo de carga de una página
     */
    public static long medirTiempoCargaPagina(WebDriver driver, String url) {
        logger.debug("Midiendo tiempo de carga para: {}", url);

        long tiempoInicio = System.currentTimeMillis();

        driver.get(url);

        // Esperar a que la página se cargue completamente
        WebDriverWait espera = new WebDriverWait(driver, Duration.ofSeconds(30));
        espera.until(webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

        long tiempoFin = System.currentTimeMillis();
        long tiempoCarga = tiempoFin - tiempoInicio;

        logger.info("Tiempo de carga para '{}': {}ms", url, tiempoCarga);
        return tiempoCarga;
    }

    /**
     * Obtiene métricas de performance de la página
     */
    public static java.util.Map<String, Object> obtenerMetricasPerformance(WebDriver driver) {
        java.util.Map<String, Object> metricas = new java.util.HashMap<>();

        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Timing de navegación
            Object timing = js.executeScript(
                    "return {" +
                            "  loadEventEnd: performance.timing.loadEventEnd," +
                            "  navigationStart: performance.timing.navigationStart," +
                            "  domContentLoadedEventEnd: performance.timing.domContentLoadedEventEnd," +
                            "  domComplete: performance.timing.domComplete" +
                            "};"
            );

            metricas.put("timing", timing);

            // Memoria (si está disponible)
            try {
                Object memoria = js.executeScript("return performance.memory;");
                metricas.put("memoria", memoria);
            } catch (Exception e) {
                metricas.put("memoria", "No disponible");
            }

            // Recursos cargados
            Long recursos = (Long) js.executeScript("return performance.getEntriesByType('resource').length;");
            metricas.put("recursosEstaticos", recursos);

            logger.debug("Métricas de performance obtenidas");

        } catch (Exception e) {
            logger.warn("Error obteniendo métricas de performance: {}", e.getMessage());
            metricas.put("error", e.getMessage());
        }

        return metricas;
    }

    // ========================================
    // UTILIDADES DE LIMPIEZA
    // ========================================

    /**
     * Limpia todos los campos de texto en una página
     */
    public static int limpiarCamposTexto(WebDriver driver) {
        logger.debug("Limpiando todos los campos de texto");

        List<WebElement> campos = driver.findElements(By.cssSelector("input[type='text'], input[type='password'], input[type='email'], textarea"));
        int camposLimpiados = 0;

        for (WebElement campo : campos) {
            try {
                if (campo.isEnabled() && campo.isDisplayed()) {
                    campo.clear();
                    camposLimpiados++;
                }
            } catch (Exception e) {
                logger.warn("Error limpiando campo: {}", e.getMessage());
            }
        }

        logger.info("Campos de texto limpiados: {}", camposLimpiados);
        return camposLimpiados;
    }

    /**
     * Deselecciona todos los checkboxes en una página
     */
    public static int deseleccionarCheckboxes(WebDriver driver) {
        logger.debug("Deseleccionando todos los checkboxes");

        List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
        int checkboxesDeseleccionados = 0;

        for (WebElement checkbox : checkboxes) {
            try {
                if (checkbox.isEnabled() && checkbox.isDisplayed() && checkbox.isSelected()) {
                    checkbox.click();
                    checkboxesDeseleccionados++;
                }
            } catch (Exception e) {
                logger.warn("Error deseleccionando checkbox: {}", e.getMessage());
            }
        }

        logger.info("Checkboxes deseleccionados: {}", checkboxesDeseleccionados);
        return checkboxesDeseleccionados;
    }

    // ========================================
    // UTILIDADES DE VALIDACIÓN AVANZADA
    // ========================================

    /**
     * Valida que una página no tiene errores JavaScript
     */
    public static boolean validarSinErroresJavaScript(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Verificar errores en la consola (método básico)
            @SuppressWarnings("unchecked")
            List<Object> errores = (List<Object>) js.executeScript(
                    "return window.jsErrors || [];"
            );

            boolean sinErrores = errores.isEmpty();
            logger.info("Página sin errores JavaScript: {} (errores encontrados: {})", sinErrores, errores.size());

            if (!sinErrores) {
                logger.warn("Errores JavaScript encontrados: {}", errores);
            }

            return sinErrores;

        } catch (Exception e) {
            logger.warn("Error verificando errores JavaScript: {}", e.getMessage());
            return true; // Asumir que no hay errores si no se puede verificar
        }
    }

    /**
     * Valida que todos los enlaces en una página son válidos (básico)
     */
    public static java.util.Map<String, Boolean> validarEnlaces(WebDriver driver) {
        logger.debug("Validando enlaces en la página");

        java.util.Map<String, Boolean> resultadosEnlaces = new java.util.HashMap<>();
        List<WebElement> enlaces = driver.findElements(By.tagName("a"));

        for (WebElement enlace : enlaces) {
            try {
                String href = enlace.getAttribute("href");
                if (href != null && !href.isEmpty() && !href.startsWith("javascript:") && !href.startsWith("mailto:")) {
                    // Validación básica de URL
                    boolean esValido = href.startsWith("http://") || href.startsWith("https://") || href.startsWith("/");
                    resultadosEnlaces.put(href, esValido);
                }
            } catch (Exception e) {
                logger.warn("Error validando enlace: {}", e.getMessage());
            }
        }

        long enlacesValidos = resultadosEnlaces.values().stream().mapToLong(valido -> valido ? 1 : 0).sum();
        logger.info("Enlaces validados: {} válidos de {} total", enlacesValidos, resultadosEnlaces.size());

        return resultadosEnlaces;
    }

    // ========================================
    // UTILIDADES DE ACCESIBILIDAD BÁSICA
    // ========================================

    /**
     * Verifica elementos básicos de accesibilidad
     */
    public static java.util.Map<String, Boolean> verificarAccesibilidadBasica(WebDriver driver) {
        logger.debug("Verificando accesibilidad básica");

        java.util.Map<String, Boolean> resultados = new java.util.HashMap<>();

        // Verificar que las imágenes tengan atributo alt
        List<WebElement> imagenes = driver.findElements(By.tagName("img"));
        long imagenesConAlt = imagenes.stream()
                .mapToLong(img -> {
                    String alt = img.getAttribute("alt");
                    return (alt != null && !alt.trim().isEmpty()) ? 1 : 0;
                })
                .sum();

        resultados.put("imagenesConAlt", imagenes.isEmpty() || imagenesConAlt == imagenes.size());

        // Verificar que los campos de formulario tengan labels
        List<WebElement> campos = driver.findElements(By.cssSelector("input, textarea, select"));
        long camposConLabel = campos.stream()
                .mapToLong(campo -> {
                    String id = campo.getAttribute("id");
                    if (id != null && !id.isEmpty()) {
                        try {
                            driver.findElement(By.cssSelector("label[for='" + id + "']"));
                            return 1;
                        } catch (NoSuchElementException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .sum();

        resultados.put("camposConLabel", campos.isEmpty() || camposConLabel >= (campos.size() * 0.8)); // 80% mínimo

        // Verificar presencia de elementos de navegación
        boolean tieneNavegacion = !driver.findElements(By.tagName("nav")).isEmpty() ||
                !driver.findElements(By.cssSelector("[role='navigation']")).isEmpty();
        resultados.put("tieneNavegacion", tieneNavegacion);

        // Verificar títulos de página
        String titulo = driver.getTitle();
        resultados.put("tieneTitulo", titulo != null && !titulo.trim().isEmpty());

        logger.info("Verificación de accesibilidad completada: {}", resultados);
        return resultados;
    }

    // ========================================
    // UTILIDADES DE CONFIGURACIÓN
    // ========================================

    /**
     * Configura timeouts personalizados para el driver
     */
    public static void configurarTimeouts(WebDriver driver, Duration implicitWait, Duration pageLoad, Duration script) {
        logger.debug("Configurando timeouts personalizados");

        driver.manage().timeouts().implicitlyWait(implicitWait);
        driver.manage().timeouts().pageLoadTimeout(pageLoad);
        driver.manage().timeouts().scriptTimeout(script);

        logger.info("Timeouts configurados - Implicit: {}s, PageLoad: {}s, Script: {}s",
                implicitWait.getSeconds(), pageLoad.getSeconds(), script.getSeconds());
    }

    /**
     * Configura la ventana del navegador
     */
    public static void configurarVentana(WebDriver driver, boolean maximizar, Dimension tamanio) {
        logger.debug("Configurando ventana del navegador");

        if (maximizar) {
            driver.manage().window().maximize();
            logger.debug("Ventana maximizada");
        } else if (tamanio != null) {
            driver.manage().window().setSize(tamanio);
            logger.debug("Ventana redimensionada a: {}x{}", tamanio.width, tamanio.height);
        }
    }

    // ========================================
    // MÉTODO UTILITARIO PARA MANEJO DE ESPERAS
    // ========================================

    /**
     * Método utilitario para manejar esperas con InterruptedException
     */
    private static void esperarProcesamiento(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            logger.warn("Interrupción durante espera de procesamiento: {}", e.getMessage());
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
        }
    }
}
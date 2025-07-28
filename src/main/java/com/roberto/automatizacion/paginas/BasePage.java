package com.roberto.automatizacion.paginas;

import com.roberto.automatizacion.core.DriverManager;
import com.roberto.automatizacion.config.ConfigManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Clase base para todos los Page Objects
 * Implementa patrones: Page Object Model, Template Method, Strategy
 * Principios SOLID aplicados: SRP, OCP, LSP, ISP, DIP
 *
 * @author Roberto Rivas Lopez
 */
public abstract class BasePage {

    protected static final Logger logger = LoggerFactory.getLogger(BasePage.class);
    protected final WebDriver driver;
    protected final WebDriverWait esperaExplicita;
    protected final Actions acciones;
    protected final ConfigManager config;

    // Timeouts configurables
    protected static final Duration TIMEOUT_CORTO = Duration.ofSeconds(5);
    protected static final Duration TIMEOUT_MEDIO = Duration.ofSeconds(15);
    protected static final Duration TIMEOUT_LARGO = Duration.ofSeconds(30);

    /**
     * Constructor que inicializa los componentes básicos de la página
     */
    protected BasePage() {
        this.driver = DriverManager.getInstancia().getDriver();
        this.config = ConfigManager.getInstancia();
        this.esperaExplicita = new WebDriverWait(driver, Duration.ofSeconds(config.obtenerTimeout()));
        this.acciones = new Actions(driver);

        // Inicializar elementos de la página usando PageFactory
        PageFactory.initElements(driver, this);

        logger.debug("Página {} inicializada", this.getClass().getSimpleName());
    }

    // ========================================
    // MÉTODOS DE NAVEGACIÓN
    // ========================================

    /**
     * Navega a una URL específica
     */
    protected void navegarA(String url) {
        try {
            logger.info("Navegando a: {}", url);
            driver.get(url);
            esperarCargaCompleta();
        } catch (Exception e) {
            logger.error("Error navegando a {}: {}", url, e.getMessage());
            throw new RuntimeException("Fallo en navegación a: " + url, e);
        }
    }

    /**
     * Obtiene la URL actual
     */
    protected String obtenerUrlActual() {
        return driver.getCurrentUrl();
    }

    /**
     * Obtiene el título de la página
     */
    protected String obtenerTitulo() {
        return driver.getTitle();
    }

    /**
     * Navega hacia atrás
     */
    protected void navegarAtras() {
        logger.info("Navegando hacia atrás");
        driver.navigate().back();
        esperarCargaCompleta();
    }

    /**
     * Navega hacia adelante
     */
    protected void navegarAdelante() {
        logger.info("Navegando hacia adelante");
        driver.navigate().forward();
        esperarCargaCompleta();
    }

    /**
     * Refresca la página
     */
    protected void refrescarPagina() {
        logger.info("Refrescando página");
        driver.navigate().refresh();
        esperarCargaCompleta();
    }

    // ========================================
    // MÉTODOS DE ESPERA Y LOCALIZACIÓN
    // ========================================

    /**
     * Espera a que un elemento sea visible
     */
    protected WebElement esperarElementoVisible(By localizador) {
        return esperarElementoVisible(localizador, TIMEOUT_MEDIO);
    }

    /**
     * Espera a que un elemento sea visible con timeout personalizado
     */
    protected WebElement esperarElementoVisible(By localizador, Duration timeout) {
        try {
            WebDriverWait espera = new WebDriverWait(driver, timeout);
            WebElement elemento = espera.until(ExpectedConditions.visibilityOfElementLocated(localizador));
            logger.debug("Elemento visible encontrado: {}", localizador);
            return elemento;
        } catch (TimeoutException e) {
            logger.error("Timeout esperando elemento visible: {}", localizador);
            throw new RuntimeException("Elemento no visible después de " + timeout.getSeconds() + "s: " + localizador, e);
        }
    }

    /**
     * Espera a que un elemento sea clickeable
     */
    protected WebElement esperarElementoClickeable(By localizador) {
        return esperarElementoClickeable(localizador, TIMEOUT_MEDIO);
    }

    /**
     * Espera a que un elemento sea clickeable con timeout personalizado
     */
    protected WebElement esperarElementoClickeable(By localizador, Duration timeout) {
        try {
            WebDriverWait espera = new WebDriverWait(driver, timeout);
            WebElement elemento = espera.until(ExpectedConditions.elementToBeClickable(localizador));
            logger.debug("Elemento clickeable encontrado: {}", localizador);
            return elemento;
        } catch (TimeoutException e) {
            logger.error("Timeout esperando elemento clickeable: {}", localizador);
            throw new RuntimeException("Elemento no clickeable después de " + timeout.getSeconds() + "s: " + localizador, e);
        }
    }

    /**
     * Espera a que un elemento esté presente en el DOM
     */
    protected WebElement esperarElementoPresente(By localizador) {
        return esperarElementoPresente(localizador, TIMEOUT_MEDIO);
    }

    /**
     * Espera a que un elemento esté presente con timeout personalizado
     */
    protected WebElement esperarElementoPresente(By localizador, Duration timeout) {
        try {
            WebDriverWait espera = new WebDriverWait(driver, timeout);
            WebElement elemento = espera.until(ExpectedConditions.presenceOfElementLocated(localizador));
            logger.debug("Elemento presente encontrado: {}", localizador);
            return elemento;
        } catch (TimeoutException e) {
            logger.error("Timeout esperando elemento presente: {}", localizador);
            throw new RuntimeException("Elemento no presente después de " + timeout.getSeconds() + "s: " + localizador, e);
        }
    }

    /**
     * Espera a que un elemento desaparezca
     */
    protected boolean esperarElementoInvisible(By localizador) {
        return esperarElementoInvisible(localizador, TIMEOUT_MEDIO);
    }

    /**
     * Espera a que un elemento desaparezca con timeout personalizado
     */
    protected boolean esperarElementoInvisible(By localizador, Duration timeout) {
        try {
            WebDriverWait espera = new WebDriverWait(driver, timeout);
            boolean invisible = espera.until(ExpectedConditions.invisibilityOfElementLocated(localizador));
            logger.debug("Elemento invisible: {}", localizador);
            return invisible;
        } catch (TimeoutException e) {
            logger.warn("Timeout esperando que elemento sea invisible: {}", localizador);
            return false;
        }
    }

    /**
     * Espera a que el texto esté presente en un elemento
     */
    protected boolean esperarTextoEnElemento(By localizador, String texto) {
        try {
            boolean textoPresente = esperaExplicita.until(
                    ExpectedConditions.textToBePresentInElementLocated(localizador, texto));
            logger.debug("Texto '{}' encontrado en elemento: {}", texto, localizador);
            return textoPresente;
        } catch (TimeoutException e) {
            logger.error("Timeout esperando texto '{}' en elemento: {}", texto, localizador);
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE INTERACCIÓN CON ELEMENTOS
    // ========================================

    /**
     * Click seguro en un elemento
     */
    protected void clickSeguro(By localizador) {
        try {
            WebElement elemento = esperarElementoClickeable(localizador);
            desplazarseAElemento(elemento);
            elemento.click();
            logger.debug("Click realizado en: {}", localizador);
        } catch (Exception e) {
            logger.error("Error haciendo click en {}: {}", localizador, e.getMessage());
            // Intentar click con JavaScript como fallback
            clickConJavaScript(localizador);
        }
    }

    /**
     * Click usando JavaScript como alternativa
     */
    protected void clickConJavaScript(By localizador) {
        try {
            WebElement elemento = esperarElementoPresente(localizador);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", elemento);
            logger.debug("Click con JavaScript realizado en: {}", localizador);
        } catch (Exception e) {
            logger.error("Error haciendo click con JavaScript en {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo hacer click en: " + localizador, e);
        }
    }

    /**
     * Escribe texto en un campo de forma segura
     */
    protected void escribirTexto(By localizador, String texto) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            desplazarseAElemento(elemento);
            elemento.clear();
            elemento.sendKeys(texto);
            logger.debug("Texto '{}' escrito en: {}", texto, localizador);
        } catch (Exception e) {
            logger.error("Error escribiendo texto en {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo escribir texto en: " + localizador, e);
        }
    }

    /**
     * Escribe texto lentamente (útil para campos con validación en tiempo real)
     */
    protected void escribirTextoLento(By localizador, String texto, int delayMilisegundos) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            desplazarseAElemento(elemento);
            elemento.clear();

            for (char caracter : texto.toCharArray()) {
                elemento.sendKeys(String.valueOf(caracter));
                esperarProcesamiento(delayMilisegundos);
            }

            logger.debug("Texto '{}' escrito lentamente en: {}", texto, localizador);
        } catch (Exception e) {
            logger.error("Error escribiendo texto lento en {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo escribir texto lento en: " + localizador, e);
        }
    }

    /**
     * Obtiene el texto de un elemento
     */
    protected String obtenerTexto(By localizador) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            String texto = elemento.getText();
            logger.debug("Texto obtenido '{}' de: {}", texto, localizador);
            return texto;
        } catch (Exception e) {
            logger.error("Error obteniendo texto de {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo obtener texto de: " + localizador, e);
        }
    }

    /**
     * Obtiene el valor de un atributo
     */
    protected String obtenerAtributo(By localizador, String atributo) {
        try {
            WebElement elemento = esperarElementoPresente(localizador);
            String valor = elemento.getAttribute(atributo);
            logger.debug("Atributo '{}' = '{}' obtenido de: {}", atributo, valor, localizador);
            return valor;
        } catch (Exception e) {
            logger.error("Error obteniendo atributo '{}' de {}: {}", atributo, localizador, e.getMessage());
            throw new RuntimeException("No se pudo obtener atributo de: " + localizador, e);
        }
    }

    /**
     * Verifica si un elemento está visible
     */
    protected boolean esElementoVisible(By localizador) {
        try {
            WebElement elemento = driver.findElement(localizador);
            boolean visible = elemento.isDisplayed();
            logger.debug("Elemento {} es visible: {}", localizador, visible);
            return visible;
        } catch (NoSuchElementException e) {
            logger.debug("Elemento {} no encontrado", localizador);
            return false;
        }
    }

    /**
     * Verifica si un elemento está habilitado
     */
    protected boolean esElementoHabilitado(By localizador) {
        try {
            WebElement elemento = esperarElementoPresente(localizador);
            boolean habilitado = elemento.isEnabled();
            logger.debug("Elemento {} está habilitado: {}", localizador, habilitado);
            return habilitado;
        } catch (Exception e) {
            logger.error("Error verificando si elemento está habilitado {}: {}", localizador, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un elemento está seleccionado (checkbox, radio button)
     */
    protected boolean esElementoSeleccionado(By localizador) {
        try {
            WebElement elemento = esperarElementoPresente(localizador);
            boolean seleccionado = elemento.isSelected();
            logger.debug("Elemento {} está seleccionado: {}", localizador, seleccionado);
            return seleccionado;
        } catch (Exception e) {
            logger.error("Error verificando si elemento está seleccionado {}: {}", localizador, e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS PARA DROPDOWNS
    // ========================================

    /**
     * Selecciona una opción de dropdown por texto visible
     */
    protected void seleccionarPorTexto(By localizador, String texto) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            Select dropdown = new Select(elemento);
            dropdown.selectByVisibleText(texto);
            logger.debug("Opción '{}' seleccionada en dropdown: {}", texto, localizador);
        } catch (Exception e) {
            logger.error("Error seleccionando por texto '{}' en {}: {}", texto, localizador, e.getMessage());
            throw new RuntimeException("No se pudo seleccionar opción por texto: " + localizador, e);
        }
    }

    /**
     * Selecciona una opción de dropdown por valor
     */
    protected void seleccionarPorValor(By localizador, String valor) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            Select dropdown = new Select(elemento);
            dropdown.selectByValue(valor);
            logger.debug("Opción con valor '{}' seleccionada en dropdown: {}", valor, localizador);
        } catch (Exception e) {
            logger.error("Error seleccionando por valor '{}' en {}: {}", valor, localizador, e.getMessage());
            throw new RuntimeException("No se pudo seleccionar opción por valor: " + localizador, e);
        }
    }

    /**
     * Selecciona una opción de dropdown por índice
     */
    protected void seleccionarPorIndice(By localizador, int indice) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            Select dropdown = new Select(elemento);
            dropdown.selectByIndex(indice);
            logger.debug("Opción en índice {} seleccionada en dropdown: {}", indice, localizador);
        } catch (Exception e) {
            logger.error("Error seleccionando por índice {} en {}: {}", indice, localizador, e.getMessage());
            throw new RuntimeException("No se pudo seleccionar opción por índice: " + localizador, e);
        }
    }

    // ========================================
    // MÉTODOS DE DESPLAZAMIENTO Y ACCIONES
    // ========================================

    /**
     * Se desplaza hasta un elemento para que sea visible
     */
    protected void desplazarseAElemento(WebElement elemento) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", elemento);
            esperarProcesamiento(500); // Pequeña pausa para que el scroll se complete
            logger.debug("Desplazado a elemento");
        } catch (Exception e) {
            logger.warn("Error desplazándose a elemento: {}", e.getMessage());
        }
    }

    /**
     * Se desplaza hasta un elemento por localizador
     */
    protected void desplazarseAElemento(By localizador) {
        WebElement elemento = esperarElementoPresente(localizador);
        desplazarseAElemento(elemento);
    }

    /**
     * Realiza hover sobre un elemento
     */
    protected void hacerHover(By localizador) {
        try {
            WebElement elemento = esperarElementoVisible(localizador);
            acciones.moveToElement(elemento).perform();
            logger.debug("Hover realizado en: {}", localizador);
        } catch (Exception e) {
            logger.error("Error haciendo hover en {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo hacer hover en: " + localizador, e);
        }
    }

    /**
     * Realiza doble click en un elemento
     */
    protected void dobleClick(By localizador) {
        try {
            WebElement elemento = esperarElementoClickeable(localizador);
            desplazarseAElemento(elemento);
            acciones.doubleClick(elemento).perform();
            logger.debug("Doble click realizado en: {}", localizador);
        } catch (Exception e) {
            logger.error("Error haciendo doble click en {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo hacer doble click en: " + localizador, e);
        }
    }

    /**
     * Realiza click derecho en un elemento
     */
    protected void clickDerecho(By localizador) {
        try {
            WebElement elemento = esperarElementoClickeable(localizador);
            desplazarseAElemento(elemento);
            acciones.contextClick(elemento).perform();
            logger.debug("Click derecho realizado en: {}", localizador);
        } catch (Exception e) {
            logger.error("Error haciendo click derecho en {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudo hacer click derecho en: " + localizador, e);
        }
    }

    // ========================================
    // MÉTODOS DE VENTANAS Y PESTAÑAS
    // ========================================

    /**
     * Cambia a una nueva ventana/pestaña
     */
    protected void cambiarANuevaVentana() {
        try {
            String ventanaOriginal = driver.getWindowHandle();
            Set<String> ventanas = driver.getWindowHandles();

            for (String ventana : ventanas) {
                if (!ventana.equals(ventanaOriginal)) {
                    driver.switchTo().window(ventana);
                    logger.debug("Cambiado a nueva ventana");
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error cambiando a nueva ventana: {}", e.getMessage());
            throw new RuntimeException("No se pudo cambiar a nueva ventana", e);
        }
    }

    /**
     * Cierra la ventana actual y vuelve a la original
     */
    protected void cerrarVentanaYVolverAOriginal() {
        try {
            driver.close();
            Set<String> ventanas = driver.getWindowHandles();
            driver.switchTo().window(ventanas.iterator().next());
            logger.debug("Ventana cerrada y regresado a ventana original");
        } catch (Exception e) {
            logger.error("Error cerrando ventana: {}", e.getMessage());
            throw new RuntimeException("No se pudo cerrar ventana", e);
        }
    }

    // ========================================
    // MÉTODOS DE UTILIDAD
    // ========================================

    /**
     * Espera a que la página se cargue completamente
     */
    protected void esperarCargaCompleta() {
        try {
            esperaExplicita.until(webDriver ->
                    ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            logger.debug("Página cargada completamente");
        } catch (Exception e) {
            logger.warn("Timeout esperando carga completa de página: {}", e.getMessage());
        }
    }

    /**
     * Ejecuta JavaScript en la página
     */
    protected Object ejecutarJavaScript(String script, Object... argumentos) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object resultado = js.executeScript(script, argumentos);
            logger.debug("JavaScript ejecutado: {}", script);
            return resultado;
        } catch (Exception e) {
            logger.error("Error ejecutando JavaScript '{}': {}", script, e.getMessage());
            throw new RuntimeException("Error ejecutando JavaScript", e);
        }
    }

    /**
     * Toma una captura de pantalla
     */
    protected byte[] tomarCapturaPantalla() {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            byte[] captura = screenshot.getScreenshotAs(OutputType.BYTES);
            logger.debug("Captura de pantalla tomada");
            return captura;
        } catch (Exception e) {
            logger.error("Error tomando captura de pantalla: {}", e.getMessage());
            throw new RuntimeException("No se pudo tomar captura de pantalla", e);
        }
    }

    /**
     * Obtiene todos los elementos que coinciden con un localizador
     */
    protected List<WebElement> obtenerElementos(By localizador) {
        try {
            List<WebElement> elementos = driver.findElements(localizador);
            logger.debug("Encontrados {} elementos para: {}", elementos.size(), localizador);
            return elementos;
        } catch (Exception e) {
            logger.error("Error obteniendo elementos para {}: {}", localizador, e.getMessage());
            throw new RuntimeException("No se pudieron obtener elementos", e);
        }
    }

    /**
     * Método abstracto que debe implementar cada página específica
     * para validar que la página se cargó correctamente
     */
    public abstract boolean esPaginaCorrecta();

    /**
     * Método template para validar que la página está lista
     */
    public final boolean validarPagina() {
        try {
            esperarCargaCompleta();
            boolean paginaCorrecta = esPaginaCorrecta();

            if (paginaCorrecta) {
                logger.info("Página {} validada correctamente", this.getClass().getSimpleName());
            } else {
                logger.error("Validación fallida para página {}", this.getClass().getSimpleName());
            }

            return paginaCorrecta;
        } catch (Exception e) {
            logger.error("Error validando página {}: {}", this.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODO UTILITARIO PARA MANEJO DE ESPERAS
    // ========================================

    /**
     * Método utilitario para manejar esperas con InterruptedException
     */
    protected void esperarProcesamiento(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            logger.warn("Interrupción durante espera de procesamiento: {}", e.getMessage());
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
        }
    }
}
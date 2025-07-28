package com.roberto.automatizacion.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import io.qameta.allure.Step;

import java.util.List;

/**
 * Page Object para la página principal/home
 * Ejemplo de página más compleja con múltiples secciones
 *
 * @author Roberto Rivas Lopez
 */
public class PaginaInicio extends PaginaBase {

    // ========================================
    // LOCALIZADORES DE NAVEGACIÓN
    // ========================================

    @FindBy(css = ".navbar")
    private WebElement barraNavegacion;

    @FindBy(css = ".logo")
    private WebElement logo;

    @FindBy(css = ".user-menu")
    private WebElement menuUsuario;

    @FindBy(css = ".logout-button")
    private WebElement botonCerrarSesion;

    @FindBy(css = ".profile-link")
    private WebElement enlacePerfil;

    // ========================================
    // LOCALIZADORES DE CONTENIDO PRINCIPAL
    // ========================================

    @FindBy(css = ".welcome-message")
    private WebElement mensajeBienvenida;

    @FindBy(css = ".main-content")
    private WebElement contenidoPrincipal;

    @FindBy(css = ".dashboard-widgets")
    private WebElement widgetsPanelControl;

    @FindBy(css = ".notification-panel")
    private WebElement panelNotificaciones;

    // ========================================
    // LOCALIZADORES DE MENÚS
    // ========================================

    @FindBy(css = ".main-menu li")
    private List<WebElement> elementosMenuPrincipal;

    @FindBy(css = ".sidebar")
    private WebElement barraLateral;

    @FindBy(xpath = "//nav//a[contains(text(), 'Products')]")
    private WebElement menuProductos;

    @FindBy(xpath = "//nav//a[contains(text(), 'Services')]")
    private WebElement menuServicios;

    @FindBy(xpath = "//nav//a[contains(text(), 'About')]")
    private WebElement menuAcercaDe;

    @FindBy(xpath = "//nav//a[contains(text(), 'Contact')]")
    private WebElement menuContacto;

    // ========================================
    // LOCALIZADORES USANDO By
    // ========================================

    private static final By BARRA_NAVEGACION_BY = By.cssSelector(".navbar");
    private static final By LOGO_BY = By.cssSelector(".logo");
    private static final By MENU_USUARIO_BY = By.cssSelector(".user-menu");
    private static final By MENSAJE_BIENVENIDA_BY = By.cssSelector(".welcome-message");
    private static final By CONTENIDO_PRINCIPAL_BY = By.cssSelector(".main-content");
    private static final By BOTON_CERRAR_SESION_BY = By.cssSelector(".logout-button");
    private static final By PANEL_NOTIFICACIONES_BY = By.cssSelector(".notification-panel");
    private static final By ELEMENTOS_MENU_BY = By.cssSelector(".main-menu li");

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public PaginaInicio() {
        super();
        logger.info("PaginaInicio inicializada");
    }

    // ========================================
    // MÉTODOS DE NAVEGACIÓN
    // ========================================

    /**
     * Navega directamente a la página principal
     */
    @Step("Navegar a página principal")
    public PaginaInicio navegarAInicio() {
        String urlInicio = configuracion.obtenerUrlBase();
        navegarA(urlInicio);
        return this;
    }

    /**
     * Hace click en el logo para ir a inicio
     */
    @Step("Hacer click en logo")
    public PaginaInicio clickLogo() {
        logger.info("Haciendo click en logo");
        clickSeguro(LOGO_BY);
        return this;
    }

    // ========================================
    // MÉTODOS DE MENÚ DE USUARIO
    // ========================================

    /**
     * Hace click en el menú de usuario
     */
    @Step("Abrir menú de usuario")
    public PaginaInicio abrirMenuUsuario() {
        logger.info("Abriendo menú de usuario");
        clickSeguro(MENU_USUARIO_BY);
        return this;
    }

    /**
     * Hace click en el enlace de perfil
     */
    @Step("Ir a perfil de usuario")
    public void irAPerfil() {
        logger.info("Navegando a perfil de usuario");
        abrirMenuUsuario();
        clickSeguro(By.cssSelector(".profile-link"));
    }

    /**
     * Cierra la sesión del usuario
     */
    @Step("Cerrar sesión")
    public void cerrarSesion() {
        logger.info("Cerrando sesión de usuario");
        abrirMenuUsuario();
        clickSeguro(BOTON_CERRAR_SESION_BY);

        // Esperar a que se procese el logout
        esperarProcesamiento(2000);
    }

    // ========================================
    // MÉTODOS DE NAVEGACIÓN DEL MENÚ PRINCIPAL
    // ========================================

    /**
     * Navega a la sección de productos
     */
    @Step("Navegar a productos")
    public void navegarAProductos() {
        logger.info("Navegando a sección productos");
        clickSeguro(By.xpath("//nav//a[contains(text(), 'Products')]"));
    }

    /**
     * Navega a la sección de servicios
     */
    @Step("Navegar a servicios")
    public void navegarAServicios() {
        logger.info("Navegando a sección servicios");
        clickSeguro(By.xpath("//nav//a[contains(text(), 'Services')]"));
    }

    /**
     * Navega a la sección acerca de
     */
    @Step("Navegar a acerca de")
    public void navegarAAcercaDe() {
        logger.info("Navegando a sección acerca de");
        clickSeguro(By.xpath("//nav//a[contains(text(), 'About')]"));
    }

    /**
     * Navega a la sección de contacto
     */
    @Step("Navegar a contacto")
    public void navegarAContacto() {
        logger.info("Navegando a sección contacto");
        clickSeguro(By.xpath("//nav//a[contains(text(), 'Contact')]"));
    }

    /**
     * Navega a una sección específica por nombre
     */
    @Step("Navegar a sección: {nombreSeccion}")
    public void navegarASeccion(String nombreSeccion) {
        logger.info("Navegando a sección: {}", nombreSeccion);
        By localizadorSeccion = By.xpath("//nav//a[contains(text(), '" + nombreSeccion + "')]");
        clickSeguro(localizadorSeccion);
    }

    // ========================================
    // MÉTODOS DE VERIFICACIÓN
    // ========================================

    /**
     * Verifica si el usuario está logueado
     */
    @Step("Verificar si usuario está logueado")
    public boolean esUsuarioLogueado() {
        boolean logueado = esElementoVisible(MENU_USUARIO_BY) &&
                esElementoVisible(MENSAJE_BIENVENIDA_BY);
        logger.info("Usuario logueado: {}", logueado);
        return logueado;
    }

    /**
     * Obtiene el mensaje de bienvenida
     */
    @Step("Obtener mensaje de bienvenida")
    public String obtenerMensajeBienvenida() {
        if (esElementoVisible(MENSAJE_BIENVENIDA_BY)) {
            String mensaje = obtenerTexto(MENSAJE_BIENVENIDA_BY);
            logger.info("Mensaje de bienvenida: {}", mensaje);
            return mensaje;
        }
        return "";
    }

    /**
     * Verifica si la barra de navegación está visible
     */
    @Step("Verificar barra de navegación visible")
    public boolean esBarraNavegacionVisible() {
        boolean visible = esElementoVisible(BARRA_NAVEGACION_BY);
        logger.debug("Barra navegación visible: {}", visible);
        return visible;
    }

    /**
     * Verifica si el contenido principal está cargado
     */
    @Step("Verificar contenido principal cargado")
    public boolean esContenidoPrincipalVisible() {
        boolean visible = esElementoVisible(CONTENIDO_PRINCIPAL_BY);
        logger.debug("Contenido principal visible: {}", visible);
        return visible;
    }

    /**
     * Obtiene la lista de elementos del menú principal
     */
    @Step("Obtener elementos del menú principal")
    public List<String> obtenerElementosMenuPrincipal() {
        List<WebElement> elementos = obtenerElementos(ELEMENTOS_MENU_BY);
        List<String> textosMenu = elementos.stream()
                .map(WebElement::getText)
                .filter(texto -> !texto.trim().isEmpty())
                .toList();

        logger.info("Elementos del menú principal: {}", textosMenu);
        return textosMenu;
    }

    /**
     * Verifica si un elemento del menú específico está presente
     */
    @Step("Verificar elemento de menú presente: {textoMenu}")
    public boolean esElementoMenuPresente(String textoMenu) {
        By localizadorMenu = By.xpath("//nav//a[contains(text(), '" + textoMenu + "')]");
        boolean presente = esElementoVisible(localizadorMenu);
        logger.debug("Elemento menú '{}' presente: {}", textoMenu, presente);
        return presente;
    }

    /**
     * Cuenta el número de notificaciones
     */
    @Step("Contar notificaciones")
    public int contarNotificaciones() {
        try {
            if (esElementoVisible(PANEL_NOTIFICACIONES_BY)) {
                List<WebElement> notificaciones = obtenerElementos(
                        By.cssSelector(".notification-panel .notification-item"));
                int cantidad = notificaciones.size();
                logger.info("Notificaciones encontradas: {}", cantidad);
                return cantidad;
            }
            return 0;
        } catch (Exception e) {
            logger.warn("Error contando notificaciones: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Verifica si hay notificaciones nuevas
     */
    @Step("Verificar notificaciones nuevas")
    public boolean hayNotificacionesNuevas() {
        try {
            boolean hayNotificaciones = esElementoVisible(By.cssSelector(".notification-badge")) ||
                    contarNotificaciones() > 0;
            logger.info("Hay notificaciones nuevas: {}", hayNotificaciones);
            return hayNotificaciones;
        } catch (Exception e) {
            logger.warn("Error verificando notificaciones nuevas: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE BÚSQUEDA
    // ========================================

    /**
     * Realiza una búsqueda usando el campo de búsqueda principal
     */
    @Step("Realizar búsqueda: {termino}")
    public void realizarBusqueda(String termino) {
        logger.info("Realizando búsqueda: {}", termino);

        By campoBusqueda = By.cssSelector(".search-input, input[type='search']");
        By botonBusqueda = By.cssSelector(".search-button, button[type='submit']");

        if (esElementoVisible(campoBusqueda)) {
            escribirTexto(campoBusqueda, termino);

            if (esElementoVisible(botonBusqueda)) {
                clickSeguro(botonBusqueda);
            } else {
                // Presionar Enter si no hay botón
                WebElement campo = esperarElementoVisible(campoBusqueda);
                campo.sendKeys(org.openqa.selenium.Keys.ENTER);
            }
        } else {
            logger.warn("Campo de búsqueda no encontrado");
        }
    }

    // ========================================
    // MÉTODOS DE WIDGETS DEL PANEL DE CONTROL
    // ========================================

    /**
     * Verifica si los widgets del panel de control están cargados
     */
    @Step("Verificar widgets del panel de control cargados")
    public boolean sonWidgetsPanelControlCargados() {
        try {
            if (esElementoVisible(By.cssSelector(".dashboard-widgets"))) {
                List<WebElement> widgets = obtenerElementos(By.cssSelector(".widget"));
                boolean cargados = !widgets.isEmpty();
                logger.info("Widgets del panel de control cargados: {} (cantidad: {})", cargados, widgets.size());
                return cargados;
            }
            return false;
        } catch (Exception e) {
            logger.warn("Error verificando widgets: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene información de un widget específico
     */
    @Step("Obtener información del widget: {nombreWidget}")
    public String obtenerInformacionWidget(String nombreWidget) {
        try {
            By localizadorWidget = By.cssSelector(".widget[data-widget='" + nombreWidget + "']");

            if (esElementoVisible(localizadorWidget)) {
                String informacion = obtenerTexto(localizadorWidget);
                logger.info("Información del widget '{}': {}", nombreWidget, informacion);
                return informacion;
            }

            logger.warn("Widget '{}' no encontrado", nombreWidget);
            return "";

        } catch (Exception e) {
            logger.error("Error obteniendo información del widget '{}': {}", nombreWidget, e.getMessage());
            return "";
        }
    }

    // ========================================
    // MÉTODOS DE VALIDACIÓN ESPECÍFICOS
    // ========================================

    /**
     * Valida que todos los elementos principales de la página estén presentes
     */
    @Step("Validar elementos principales de PaginaInicio")
    public boolean validarElementosPrincipales() {
        boolean elementosPresentes = esBarraNavegacionVisible() &&
                esElementoVisible(LOGO_BY) &&
                esContenidoPrincipalVisible() &&
                esElementoVisible(MENU_USUARIO_BY);

        logger.info("Elementos principales válidos: {}", elementosPresentes);
        return elementosPresentes;
    }

    /**
     * Valida que el usuario esté correctamente autenticado
     */
    @Step("Validar autenticación de usuario")
    public boolean validarAutenticacionUsuario() {
        boolean autenticado = esUsuarioLogueado() &&
                !obtenerMensajeBienvenida().isEmpty() &&
                esElementoVisible(BOTON_CERRAR_SESION_BY);

        logger.info("Usuario autenticado correctamente: {}", autenticado);
        return autenticado;
    }

    // ========================================
    // IMPLEMENTACIÓN DE MÉTODO ABSTRACTO
    // ========================================

    /**
     * Implementación del método abstracto de PaginaBase
     * Verifica que la página de inicio se cargó correctamente
     */
    @Override
    @Step("Verificar que es la página correcta - PaginaInicio")
    public boolean esPaginaCorrecta() {
        try {
            // Verificar URL (puede ser la raíz o contener "home" o "dashboard")
            String urlActual = obtenerUrlActual();
            boolean urlCorrecta = urlActual.equals(configuracion.obtenerUrlBase()) ||
                    urlActual.contains("/home") ||
                    urlActual.contains("/dashboard") ||
                    urlActual.contains("/inicio") ||
                    urlActual.endsWith("/");

            // Verificar título
            String titulo = obtenerTitulo();
            boolean tituloCorreto = titulo.toLowerCase().contains("home") ||
                    titulo.toLowerCase().contains("dashboard") ||
                    titulo.toLowerCase().contains("principal") ||
                    titulo.toLowerCase().contains("inicio");

            // Verificar elementos específicos de inicio
            boolean elementosInicio = validarElementosPrincipales();

            // Verificar que no estamos en página de login
            boolean noEsLogin = !urlActual.contains("login") &&
                    !titulo.toLowerCase().contains("login");

            boolean paginaCorrecta = urlCorrecta && tituloCorreto && elementosInicio && noEsLogin;

            logger.info("Verificación PaginaInicio - URL: {}, Título: {}, Elementos: {}, No Login: {}",
                    urlCorrecta, tituloCorreto, elementosInicio, noEsLogin);

            return paginaCorrecta;

        } catch (Exception e) {
            logger.error("Error verificando PaginaInicio: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE UTILIDAD
    // ========================================

    /**
     * Espera a que la página de inicio se cargue completamente
     */
    @Step("Esperar carga completa de PaginaInicio")
    public PaginaInicio esperarCargaCompletaInicio() {
        logger.debug("Esperando carga completa de PaginaInicio");

        esperarCargaCompleta();
        esperarElementoVisible(BARRA_NAVEGACION_BY, TIMEOUT_LARGO);
        esperarElementoVisible(CONTENIDO_PRINCIPAL_BY, TIMEOUT_MEDIO);

        // Esperar a que los elementos dinámicos se carguen
        esperarProcesamiento(1000);

        return this;
    }

    /**
     * Realiza un recorrido básico de verificación de la página
     */
    @Step("Realizar recorrido de verificación de PaginaInicio")
    public boolean realizarRecorridoVerificacion() {
        try {
            logger.info("Iniciando recorrido de verificación de PaginaInicio");

            // Verificar carga inicial
            boolean cargaInicial = esperarCargaCompletaInicio() != null && validarPagina();
            if (!cargaInicial) return false;

            // Verificar elementos principales
            boolean elementosPrincipales = validarElementosPrincipales();
            if (!elementosPrincipales) return false;

            // Verificar autenticación si aplica
            boolean autenticacion = true;
            if (esElementoVisible(MENU_USUARIO_BY)) {
                autenticacion = validarAutenticacionUsuario();
            }

            // Verificar funcionalidad básica del menú
            boolean menuFuncional = !obtenerElementosMenuPrincipal().isEmpty();

            boolean recorridoExitoso = cargaInicial && elementosPrincipales &&
                    autenticacion && menuFuncional;

            logger.info("Recorrido de verificación completado: {}", recorridoExitoso);
            return recorridoExitoso;

        } catch (Exception e) {
            logger.error("Error en recorrido de verificación: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE NAVEGACIÓN AVANZADA
    // ========================================

    /**
     * Navega usando breadcrumbs si están disponibles
     */
    @Step("Navegar usando breadcrumb: {textoBreadcrumb}")
    public void navegarPorBreadcrumb(String textoBreadcrumb) {
        try {
            By breadcrumbLocator = By.xpath("//nav[@aria-label='breadcrumb']//a[contains(text(), '" + textoBreadcrumb + "')]");

            if (esElementoVisible(breadcrumbLocator)) {
                clickSeguro(breadcrumbLocator);
                logger.info("Navegación por breadcrumb exitosa: {}", textoBreadcrumb);
            } else {
                logger.warn("Breadcrumb '{}' no encontrado", textoBreadcrumb);
            }
        } catch (Exception e) {
            logger.error("Error navegando por breadcrumb '{}': {}", textoBreadcrumb, e.getMessage());
        }
    }

    /**
     * Abre un modal o popup específico
     */
    @Step("Abrir modal: {nombreModal}")
    public boolean abrirModal(String nombreModal) {
        try {
            By triggerModal = By.cssSelector("[data-toggle='modal'][data-target*='" + nombreModal + "']");

            if (esElementoVisible(triggerModal)) {
                clickSeguro(triggerModal);

                // Esperar a que el modal aparezca
                By modal = By.cssSelector(".modal.show, .modal.in");
                boolean modalAbierto = esperarElementoVisible(modal, TIMEOUT_CORTO) != null;

                logger.info("Modal '{}' abierto: {}", nombreModal, modalAbierto);
                return modalAbierto;
            }

            logger.warn("Trigger para modal '{}' no encontrado", nombreModal);
            return false;

        } catch (Exception e) {
            logger.error("Error abriendo modal '{}': {}", nombreModal, e.getMessage());
            return false;
        }
    }

    /**
     * Cierra modal activo
     */
    @Step("Cerrar modal activo")
    public boolean cerrarModalActivo() {
        try {
            By modalActivo = By.cssSelector(".modal.show, .modal.in");
            By botonCerrar = By.cssSelector(".modal .close, .modal .btn-close");

            if (esElementoVisible(modalActivo) && esElementoVisible(botonCerrar)) {
                clickSeguro(botonCerrar);

                // Esperar a que el modal desaparezca
                boolean modalCerrado = esperarElementoInvisible(modalActivo, TIMEOUT_CORTO);

                logger.info("Modal cerrado: {}", modalCerrado);
                return modalCerrado;
            }

            logger.debug("No hay modal activo para cerrar");
            return true;

        } catch (Exception e) {
            logger.error("Error cerrando modal: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE VALIDACIÓN DE ACCESIBILIDAD
    // ========================================

    /**
     * Valida elementos básicos de accesibilidad en la página
     */
    @Step("Validar accesibilidad básica de PaginaInicio")
    public boolean validarAccesibilidadBasica() {
        logger.info("Validando accesibilidad básica de PaginaInicio");

        try {
            // Verificar que las imágenes principales tengan alt text
            List<WebElement> imagenes = obtenerElementos(By.tagName("img"));
            long imagenesConAlt = imagenes.stream()
                    .filter(img -> {
                        String alt = img.getAttribute("alt");
                        return alt != null && !alt.trim().isEmpty();
                    })
                    .count();

            boolean imagenesAccesibles = imagenes.isEmpty() || imagenesConAlt >= (imagenes.size() * 0.8);

            // Verificar elementos de navegación
            boolean tieneNavegacionAccesible = esElementoVisible(By.cssSelector("nav")) ||
                    esElementoVisible(By.cssSelector("[role='navigation']"));

            // Verificar estructura de encabezados
            boolean tieneEstructuraEncabezados = !obtenerElementos(By.cssSelector("h1, h2, h3")).isEmpty();

            boolean accesibilidadBasica = imagenesAccesibles && tieneNavegacionAccesible && tieneEstructuraEncabezados;

            logger.info("Validación accesibilidad - Imágenes: {}, Navegación: {}, Encabezados: {}",
                    imagenesAccesibles, tieneNavegacionAccesible, tieneEstructuraEncabezados);

            return accesibilidadBasica;

        } catch (Exception e) {
            logger.error("Error validando accesibilidad: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE PERFORMANCE Y MÉTRICAS
    // ========================================

    /**
     * Obtiene métricas básicas de performance de la página
     */
    @Step("Obtener métricas de performance de PaginaInicio")
    public java.util.Map<String, Object> obtenerMetricasPerformance() {
        java.util.Map<String, Object> metricas = new java.util.HashMap<>();

        try {
            // Tiempo de carga del DOM
            Object domContentLoaded = ejecutarJavaScript(
                    "return performance.timing.domContentLoadedEventEnd - performance.timing.navigationStart;"
            );
            metricas.put("tiempoCargaDOM", domContentLoaded);

            // Tiempo total de carga
            Object tiempoTotalCarga = ejecutarJavaScript(
                    "return performance.timing.loadEventEnd - performance.timing.navigationStart;"
            );
            metricas.put("tiempoTotalCarga", tiempoTotalCarga);

            // Número de recursos cargados
            Object recursosEstaticos = ejecutarJavaScript(
                    "return performance.getEntriesByType('resource').length;"
            );
            metricas.put("recursosEstaticos", recursosEstaticos);

            // Información de memoria (si está disponible)
            try {
                Object memoria = ejecutarJavaScript("return performance.memory;");
                metricas.put("memoria", memoria);
            } catch (Exception e) {
                metricas.put("memoria", "No disponible");
            }

            logger.info("Métricas de performance obtenidas: {}", metricas);

        } catch (Exception e) {
            logger.error("Error obteniendo métricas de performance: {}", e.getMessage());
            metricas.put("error", e.getMessage());
        }

        return metricas;
    }

    // ========================================
    // MÉTODOS DE UTILIDAD PARA PRUEBAS E2E
    // ========================================

    /**
     * Simula el flujo completo de un usuario típico en la página de inicio
     */
    @Step("Simular flujo de usuario típico en PaginaInicio")
    public boolean simularFlujoUsuarioTipico() {
        try {
            logger.info("Simulando flujo de usuario típico en PaginaInicio");

            // 1. Verificar que la página se carga correctamente
            if (!esperarCargaCompletaInicio().validarPagina()) {
                return false;
            }

            // 2. Verificar el estado de autenticación
            boolean usuarioLogueado = esUsuarioLogueado();
            logger.info("Usuario logueado: {}", usuarioLogueado);

            // 3. Interactuar con elementos principales
            if (usuarioLogueado) {
                // Verificar mensaje de bienvenida
                String mensajeBienvenida = obtenerMensajeBienvenida();
                logger.info("Mensaje de bienvenida verificado: {}", !mensajeBienvenida.isEmpty());

                // Verificar notificaciones si están disponibles
                if (hayNotificacionesNuevas()) {
                    int cantidadNotificaciones = contarNotificaciones();
                    logger.info("Notificaciones encontradas: {}", cantidadNotificaciones);
                }
            }

            // 4. Verificar navegación principal
            List<String> elementosMenu = obtenerElementosMenuPrincipal();
            boolean menuFuncional = !elementosMenu.isEmpty();
            logger.info("Elementos de menú disponibles: {}", elementosMenu.size());

            // 5. Verificar widgets del panel de control si están disponibles
            boolean widgetsCargados = sonWidgetsPanelControlCargados();
            logger.info("Widgets del panel de control cargados: {}", widgetsCargados);

            boolean flujoExitoso = usuarioLogueado ?
                    (menuFuncional && !obtenerMensajeBienvenida().isEmpty()) :
                    menuFuncional;

            logger.info("Flujo de usuario típico completado exitosamente: {}", flujoExitoso);
            return flujoExitoso;

        } catch (Exception e) {
            logger.error("Error en flujo de usuario típico: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE ESTADO Y INFORMACIÓN
    // ========================================

    /**
     * Obtiene un resumen del estado actual de la página
     */
    @Step("Obtener resumen del estado de PaginaInicio")
    public java.util.Map<String, Object> obtenerResumenEstado() {
        java.util.Map<String, Object> resumen = new java.util.HashMap<>();

        try {
            resumen.put("urlActual", obtenerUrlActual());
            resumen.put("titulo", obtenerTitulo());
            resumen.put("usuarioLogueado", esUsuarioLogueado());
            resumen.put("mensajeBienvenida", obtenerMensajeBienvenida());
            resumen.put("elementosMenuPrincipal", obtenerElementosMenuPrincipal());
            resumen.put("notificacionesNuevas", hayNotificacionesNuevas());
            resumen.put("cantidadNotificaciones", contarNotificaciones());
            resumen.put("widgetsCargados", sonWidgetsPanelControlCargados());
            resumen.put("paginaValidada", validarPagina());

            logger.info("Resumen de estado generado: {}", resumen.keySet());

        } catch (Exception e) {
            logger.error("Error generando resumen de estado: {}", e.getMessage());
            resumen.put("error", e.getMessage());
        }

        return resumen;
    }
}
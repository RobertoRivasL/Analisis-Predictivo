package com.roberto.automatizacion.pages;

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
public class HomePage extends BasePage {

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
    private WebElement widgetsDashboard;

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

    public HomePage() {
        super();
        logger.info("HomePage inicializada");
    }

    // ========================================
    // MÉTODOS DE NAVEGACIÓN
    // ========================================

    /**
     * Navega directamente a la página home
     */
    @Step("Navegar a página principal")
    public HomePage navegarAHome() {
        String urlHome = config.obtenerUrlBase();
        navegarA(urlHome);
        return this;
    }

    /**
     * Hace click en el logo para ir a home
     */
    @Step("Hacer click en logo")
    public HomePage clickLogo() {
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
    public HomePage abrirMenuUsuario() {
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.warn("Interrupción durante espera de logout");
        }
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
    // MÉTODOS DE WIDGETS/DASHBOARD
    // ========================================

    /**
     * Verifica si los widgets del dashboard están cargados
     */
    @Step("Verificar widgets del dashboard cargados")
    public boolean sonWidgetsDashboardCargados() {
        try {
            if (esElementoVisible(By.cssSelector(".dashboard-widgets"))) {
                List<WebElement> widgets = obtenerElementos(By.cssSelector(".widget"));
                boolean cargados = !widgets.isEmpty();
                logger.info("Widgets del dashboard cargados: {} (cantidad: {})", cargados, widgets.size());
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
    @Step("Validar elementos principales de HomePage")
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
    // IMPLEMENTACIÓN DE MÉTODO ABSTRACTO
    // ========================================

    /**
     * Implementación del método abstracto de BasePage
     * Verifica que la página home se cargó correctamente
     */
    @Override
    @Step("Verificar que es la página correcta - HomePage")
    public boolean esPaginaCorrecta() {
        try {
            // Verificar URL (puede ser la raíz o contener "home" o "dashboard")
            String urlActual = obtenerUrlActual();
            boolean urlCorrecta = urlActual.equals(config.obtenerUrlBase()) ||
                    urlActual.contains("/home") ||
                    urlActual.contains("/dashboard") ||
                    urlActual.endsWith("/");

            // Verificar título
            String titulo = obtenerTitulo();
            boolean tituloCorreto = titulo.toLowerCase().contains("home") ||
                    titulo.toLowerCase().contains("dashboard") ||
                    titulo.toLowerCase().contains("principal") ||
                    titulo.toLowerCase().contains("inicio");

            // Verificar elementos específicos de home
            boolean elementosHome = validarElementosPrincipales();

            // Verificar que no estamos en página de login
            boolean noEsLogin = !urlActual.contains("login") &&
                    !titulo.toLowerCase().contains("login");

            boolean paginaCorrecta = urlCorrecta && tituloCorreto && elementosHome && noEsLogin;

            logger.info("Verificación HomePage - URL: {}, Título: {}, Elementos: {}, No Login: {}",
                    urlCorrecta, tituloCorreto, elementosHome, noEsLogin);

            return paginaCorrecta;

        } catch (Exception e) {
            logger.error("Error verificando HomePage: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE UTILIDAD
    // ========================================

    /**
     * Espera a que la página home se cargue completamente
     */
    @Step("Esperar carga completa de HomePage")
    public HomePage esperarCargaCompletaHome() {
        logger.debug("Esperando carga completa de HomePage");

        esperarCargaCompleta();
        esperarElementoVisible(BARRA_NAVEGACION_BY, TIMEOUT_LARGO);
        esperarElementoVisible(CONTENIDO_PRINCIPAL_BY, TIMEOUT_MEDIO);

        // Esperar a que los elementos dinámicos se carguen
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.warn("Interrupción durante espera de carga");
        }

        return this;
    }

    /**
     * Realiza un recorrido básico de verificación de la página
     */
    @Step("Realizar recorrido de verificación de HomePage")
    public boolean realizarRecorridoVerificacion() {
        try {
            logger.info("Iniciando recorrido de verificación de HomePage");

            // Verificar carga inicial
            boolean cargaInicial = esperarCargaCompletaHome() != null && validarPagina();
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
}
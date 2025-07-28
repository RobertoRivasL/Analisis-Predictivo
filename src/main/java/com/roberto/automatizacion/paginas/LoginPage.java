package com.roberto.automatizacion.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import io.qameta.allure.Step;

/**
 * Page Object para la página de Login
 * Ejemplo práctico implementando BasePage
 *
 * @author Roberto Rivas Lopez
 */
public class LoginPage extends BasePage {

    // ========================================
    // LOCALIZADORES USANDO @FindBy
    // ========================================

    @FindBy(id = "username")
    private WebElement campoUsuario;

    @FindBy(id = "password")
    private WebElement campoContrasena;

    @FindBy(css = "button[type='submit']")
    private WebElement botonLogin;

    @FindBy(css = ".error-message")
    private WebElement mensajeError;

    @FindBy(xpath = "//a[contains(text(), 'Forgot Password')]")
    private WebElement enlaceOlvideContrasena;

    @FindBy(css = ".remember-me input[type='checkbox']")
    private WebElement checkboxRecordarme;

    @FindBy(css = ".login-form")
    private WebElement formularioLogin;

    // ========================================
    // LOCALIZADORES USANDO By (alternativos)
    // ========================================

    private static final By CAMPO_USUARIO_BY = By.id("username");
    private static final By CAMPO_CONTRASENA_BY = By.id("password");
    private static final By BOTON_LOGIN_BY = By.cssSelector("button[type='submit']");
    private static final By MENSAJE_ERROR_BY = By.cssSelector(".error-message");
    private static final By ENLACE_OLVIDE_CONTRASENA_BY = By.xpath("//a[contains(text(), 'Forgot Password')]");
    private static final By CHECKBOX_RECORDARME_BY = By.cssSelector(".remember-me input[type='checkbox']");
    private static final By FORMULARIO_LOGIN_BY = By.cssSelector(".login-form");

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public LoginPage() {
        super();
        logger.info("LoginPage inicializada");
    }

    // ========================================
    // MÉTODOS DE NAVEGACIÓN
    // ========================================

    /**
     * Navega directamente a la página de login
     */
    @Step("Navegar a página de login")
    public LoginPage navegarALogin() {
        String urlLogin = config.obtenerUrlBase() + "/login";
        navegarA(urlLogin);
        return this;
    }

    // ========================================
    // ACCIONES DE LA PÁGINA
    // ========================================

    /**
     * Ingresa el nombre de usuario
     */
    @Step("Ingresar usuario: {usuario}")
    public LoginPage ingresarUsuario(String usuario) {
        logger.info("Ingresando usuario: {}", usuario);
        escribirTexto(CAMPO_USUARIO_BY, usuario);
        return this;
    }

    /**
     * Ingresa la contraseña
     */
    @Step("Ingresar contraseña")
    public LoginPage ingresarContrasena(String contrasena) {
        logger.info("Ingresando contraseña");
        escribirTexto(CAMPO_CONTRASENA_BY, contrasena);
        return this;
    }

    /**
     * Hace click en el botón de login
     */
    @Step("Hacer click en botón Login")
    public void clickBotonLogin() {
        logger.info("Haciendo click en botón Login");
        clickSeguro(BOTON_LOGIN_BY);
    }

    /**
     * Marca o desmarca el checkbox "Recordarme"
     */
    @Step("Configurar recordarme: {recordar}")
    public LoginPage configurarRecordarme(boolean recordar) {
        logger.info("Configurando recordarme: {}", recordar);

        boolean estaSeleccionado = esElementoSeleccionado(CHECKBOX_RECORDARME_BY);

        if (recordar != estaSeleccionado) {
            clickSeguro(CHECKBOX_RECORDARME_BY);
        }

        return this;
    }

    /**
     * Hace click en el enlace "Olvidé mi contraseña"
     */
    @Step("Hacer click en enlace 'Olvidé mi contraseña'")
    public void clickOlvideContrasena() {
        logger.info("Haciendo click en enlace 'Olvidé mi contraseña'");
        clickSeguro(ENLACE_OLVIDE_CONTRASENA_BY);
    }

    // ========================================
    // MÉTODOS DE FLUJO COMPLETO
    // ========================================

    /**
     * Realiza login completo con credenciales
     */
    @Step("Realizar login con usuario: {usuario}")
    public void realizarLogin(String usuario, String contrasena) {
        logger.info("Realizando login completo para usuario: {}", usuario);

        ingresarUsuario(usuario)
                .ingresarContrasena(contrasena)
                .clickBotonLogin();

        // Esperar a que la página se procese
        esperarProcesamiento(2000);
    }

    /**
     * Realiza login completo con credenciales y opción recordarme
     */
    @Step("Realizar login con recordarme - Usuario: {usuario}, Recordar: {recordarme}")
    public void realizarLogin(String usuario, String contrasena, boolean recordarme) {
        logger.info("Realizando login completo con recordarme para usuario: {}", usuario);

        ingresarUsuario(usuario)
                .ingresarContrasena(contrasena)
                .configurarRecordarme(recordarme)
                .clickBotonLogin();

        // Esperar a que la página se procese
        esperarProcesamiento(2000);
    }

    /**
     * Intenta login con credenciales inválidas para verificar mensaje de error
     */
    @Step("Intentar login con credenciales inválidas")
    public void intentarLoginInvalido(String usuario, String contrasena) {
        logger.info("Intentando login inválido para verificar mensaje de error");

        ingresarUsuario(usuario)
                .ingresarContrasena(contrasena)
                .clickBotonLogin();

        // Esperar a que aparezca el mensaje de error
        esperarElementoVisible(MENSAJE_ERROR_BY, TIMEOUT_CORTO);
    }

    // ========================================
    // MÉTODOS DE VERIFICACIÓN Y VALIDACIÓN
    // ========================================

    /**
     * Verifica si el mensaje de error está visible
     */
    @Step("Verificar si mensaje de error está visible")
    public boolean esMensajeErrorVisible() {
        boolean visible = esElementoVisible(MENSAJE_ERROR_BY);
        logger.info("Mensaje de error visible: {}", visible);
        return visible;
    }

    /**
     * Obtiene el texto del mensaje de error
     */
    @Step("Obtener texto del mensaje de error")
    public String obtenerTextoMensajeError() {
        if (esMensajeErrorVisible()) {
            String mensajeError = obtenerTexto(MENSAJE_ERROR_BY);
            logger.info("Mensaje de error obtenido: {}", mensajeError);
            return mensajeError;
        }
        return "";
    }

    /**
     * Verifica si el campo usuario está habilitado
     */
    @Step("Verificar si campo usuario está habilitado")
    public boolean esCampoUsuarioHabilitado() {
        boolean habilitado = esElementoHabilitado(CAMPO_USUARIO_BY);
        logger.debug("Campo usuario habilitado: {}", habilitado);
        return habilitado;
    }

    /**
     * Verifica si el campo contraseña está habilitado
     */
    @Step("Verificar si campo contraseña está habilitado")
    public boolean esCampoContrasenaHabilitado() {
        boolean habilitado = esElementoHabilitado(CAMPO_CONTRASENA_BY);
        logger.debug("Campo contraseña habilitado: {}", habilitado);
        return habilitado;
    }

    /**
     * Verifica si el botón login está habilitado
     */
    @Step("Verificar si botón login está habilitado")
    public boolean esBotonLoginHabilitado() {
        boolean habilitado = esElementoHabilitado(BOTON_LOGIN_BY);
        logger.debug("Botón login habilitado: {}", habilitado);
        return habilitado;
    }

    /**
     * Verifica si el checkbox recordarme está seleccionado
     */
    @Step("Verificar si recordarme está seleccionado")
    public boolean esRecordarmeSeleccionado() {
        boolean seleccionado = esElementoSeleccionado(CHECKBOX_RECORDARME_BY);
        logger.debug("Recordarme seleccionado: {}", seleccionado);
        return seleccionado;
    }

    /**
     * Obtiene el valor actual del campo usuario
     */
    @Step("Obtener valor del campo usuario")
    public String obtenerValorCampoUsuario() {
        String valor = obtenerAtributo(CAMPO_USUARIO_BY, "value");
        logger.debug("Valor campo usuario: {}", valor);
        return valor;
    }

    /**
     * Verifica si la página de login se cargó correctamente
     */
    @Step("Verificar que la página de login se cargó correctamente")
    public boolean esFormularioLoginVisible() {
        boolean visible = esElementoVisible(FORMULARIO_LOGIN_BY);
        logger.info("Formulario login visible: {}", visible);
        return visible;
    }

    /**
     * Verifica si todos los elementos principales están presentes
     */
    @Step("Verificar que todos los elementos principales están presentes")
    public boolean sonTodosLosElementosVisibles() {
        boolean todosVisibles = esElementoVisible(CAMPO_USUARIO_BY) &&
                esElementoVisible(CAMPO_CONTRASENA_BY) &&
                esElementoVisible(BOTON_LOGIN_BY) &&
                esElementoVisible(FORMULARIO_LOGIN_BY);

        logger.info("Todos los elementos principales visibles: {}", todosVisibles);
        return todosVisibles;
    }

    // ========================================
    // MÉTODOS DE LIMPIEZA
    // ========================================

    /**
     * Limpia todos los campos del formulario
     */
    @Step("Limpiar formulario de login")
    public LoginPage limpiarFormulario() {
        logger.info("Limpiando formulario de login");

        if (esElementoVisible(CAMPO_USUARIO_BY)) {
            campoUsuario.clear();
        }

        if (esElementoVisible(CAMPO_CONTRASENA_BY)) {
            campoContrasena.clear();
        }

        // Si recordarme está seleccionado, deseleccionarlo
        if (esRecordarmeSeleccionado()) {
            clickSeguro(CHECKBOX_RECORDARME_BY);
        }

        return this;
    }

    // ========================================
    // IMPLEMENTACIÓN DE MÉTODO ABSTRACTO
    // ========================================

    /**
     * Implementación del método abstracto de BasePage
     * Verifica que la página de login se cargó correctamente
     */
    @Override
    @Step("Verificar que es la página correcta de login")
    public boolean esPaginaCorrecta() {
        try {
            // Verificar que estamos en la URL correcta
            String urlActual = obtenerUrlActual();
            boolean urlCorrecta = urlActual.contains("/login") || urlActual.contains("login");

            // Verificar que el título contiene "login" o similar
            String titulo = obtenerTitulo();
            boolean tituloCorreto = titulo.toLowerCase().contains("login") ||
                    titulo.toLowerCase().contains("sign in") ||
                    titulo.toLowerCase().contains("iniciar sesión");

            // Verificar que los elementos principales están presentes
            boolean elementosPresentes = sonTodosLosElementosVisibles();

            boolean paginaCorrecta = urlCorrecta && tituloCorreto && elementosPresentes;

            logger.info("Verificación página login - URL: {}, Título: {}, Elementos: {}",
                    urlCorrecta, tituloCorreto, elementosPresentes);

            return paginaCorrecta;

        } catch (Exception e) {
            logger.error("Error verificando página de login: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS DE UTILIDAD ESPECÍFICOS
    // ========================================

    /**
     * Espera a que el formulario esté completamente cargado
     */
    @Step("Esperar carga completa del formulario")
    public LoginPage esperarCargaFormulario() {
        logger.debug("Esperando carga completa del formulario de login");

        esperarElementoVisible(FORMULARIO_LOGIN_BY, TIMEOUT_LARGO);
        esperarElementoVisible(CAMPO_USUARIO_BY, TIMEOUT_MEDIO);
        esperarElementoVisible(CAMPO_CONTRASENA_BY, TIMEOUT_MEDIO);
        esperarElementoClickeable(BOTON_LOGIN_BY, TIMEOUT_MEDIO);

        return this;
    }

    /**
     * Verifica que no hay mensajes de error previos
     */
    @Step("Verificar que no hay mensajes de error previos")
    public boolean noHayMensajesErrorPrevios() {
        boolean sinErrores = !esMensajeErrorVisible();
        logger.debug("Sin mensajes de error previos: {}", sinErrores);
        return sinErrores;
    }

    /**
     * Realiza validaciones previas antes de hacer login
     */
    @Step("Realizar validaciones previas al login")
    public boolean realizarValidacionesPreLogin() {
        logger.info("Realizando validaciones previas al login");

        boolean validaciones = validarPagina() &&
                esperarCargaFormulario() != null &&
                sonTodosLosElementosVisibles() &&
                esCampoUsuarioHabilitado() &&
                esCampoContrasenaHabilitado() &&
                esBotonLoginHabilitado() &&
                noHayMensajesErrorPrevios();

        logger.info("Validaciones previas completadas: {}", validaciones);
        return validaciones;
    }

    /**
     * Método de conveniencia para login rápido con validaciones
     */
    @Step("Login rápido con validaciones - Usuario: {usuario}")
    public boolean loginRapidoConValidaciones(String usuario, String contrasena) {
        try {
            // Realizar validaciones previas
            if (!realizarValidacionesPreLogin()) {
                logger.error("Falló validaciones previas al login");
                return false;
            }

            // Realizar login
            realizarLogin(usuario, contrasena);

            // Verificar que no hay mensajes de error después del login
            esperarProcesamiento(2000); // Esperar procesamiento

            boolean loginExitoso = !esMensajeErrorVisible();
            logger.info("Login rápido completado exitosamente: {}", loginExitoso);

            return loginExitoso;

        } catch (Exception e) {
            logger.error("Error en login rápido: {}", e.getMessage());
            return false;
        }
    }

    // ========================================
    // MÉTODOS PARA DIFERENTES ESCENARIOS
    // ========================================

    /**
     * Escenario: Login con credenciales válidas
     */
    @Step("Escenario: Login exitoso")
    public boolean escenarioLoginExitoso(String usuario, String contrasena) {
        return loginRapidoConValidaciones(usuario, contrasena);
    }

    /**
     * Escenario: Login con credenciales inválidas
     */
    @Step("Escenario: Login fallido")
    public boolean escenarioLoginFallido(String usuario, String contrasena, String mensajeEsperado) {
        try {
            intentarLoginInvalido(usuario, contrasena);

            boolean hayMensajeError = esMensajeErrorVisible();
            String mensajeActual = obtenerTextoMensajeError();
            boolean mensajeCorrecto = mensajeActual.contains(mensajeEsperado);

            boolean escenarioExitoso = hayMensajeError && mensajeCorrecto;
            logger.info("Escenario login fallido completado: {}", escenarioExitoso);

            return escenarioExitoso;

        } catch (Exception e) {
            logger.error("Error en escenario login fallido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Escenario: Verificar funcionalidad recordarme
     */
    @Step("Escenario: Verificar funcionalidad recordarme")
    public boolean escenarioVerificarRecordarme() {
        try {
            // Verificar que inicialmente no está seleccionado
            boolean inicialmenteDeseleccionado = !esRecordarmeSeleccionado();

            // Seleccionar recordarme
            configurarRecordarme(true);
            boolean seleccionadoDespuesDeClick = esRecordarmeSeleccionado();

            // Deseleccionar recordarme
            configurarRecordarme(false);
            boolean deseleccionadoDespuesDeClick = !esRecordarmeSeleccionado();

            boolean funcionaCorrectamente = inicialmenteDeseleccionado &&
                    seleccionadoDespuesDeClick &&
                    deseleccionadoDespuesDeClick;

            logger.info("Funcionalidad recordarme funciona correctamente: {}", funcionaCorrectamente);
            return funcionaCorrectamente;

        } catch (Exception e) {
            logger.error("Error verificando funcionalidad recordarme: {}", e.getMessage());
            return false;
        }
    }

    // Método heredado de BasePage - no necesita redefinirse
}
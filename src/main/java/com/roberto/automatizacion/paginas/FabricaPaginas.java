package com.roberto.automatizacion.paginas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory para la creación y gestión de Page Objects
 * Implementa patrones: Factory, Singleton, Cache
 * Principios SOLID aplicados: SRP, OCP, DIP
 *
 * @author Roberto Rivas Lopez
 */
public final class PageFactory {

    private static final Logger logger = LoggerFactory.getLogger(PageFactory.class);
    private static volatile PageFactory instancia;

    // Cache de páginas para reutilización
    private final Map<Class<? extends BasePage>, BasePage> cachePages = new HashMap<>();

    // Flag para habilitar/deshabilitar cache
    private boolean cacheHabilitado = true;

    private PageFactory() {
        logger.info("PageFactory inicializado");
    }

    /**
     * Obtiene la instancia única del PageFactory (Singleton thread-safe)
     */
    public static PageFactory getInstancia() {
        if (instancia == null) {
            synchronized (PageFactory.class) {
                if (instancia == null) {
                    instancia = new PageFactory();
                }
            }
        }
        return instancia;
    }

    /**
     * Crea una nueva instancia de página del tipo especificado
     *
     * @param clasePageObject Clase del Page Object a crear
     * @return Nueva instancia de la página
     */
    @SuppressWarnings("unchecked")
    public <T extends BasePage> T crearPagina(Class<T> clasePageObject) {
        try {
            logger.debug("Creando página: {}", clasePageObject.getSimpleName());

            // Si el cache está habilitado, intentar obtener de cache primero
            if (cacheHabilitado && cachePages.containsKey(clasePageObject)) {
                T paginaCacheada = (T) cachePages.get(clasePageObject);
                logger.debug("Página obtenida del cache: {}", clasePageObject.getSimpleName());
                return paginaCacheada;
            }

            // Crear nueva instancia usando reflexión
            T nuevaPagina = clasePageObject.getDeclaredConstructor().newInstance();

            // Validar que la página se creó correctamente
            if (nuevaPagina.validarPagina()) {
                // Agregar al cache si está habilitado
                if (cacheHabilitado) {
                    cachePages.put(clasePageObject, nuevaPagina);
                    logger.debug("Página agregada al cache: {}", clasePageObject.getSimpleName());
                }

                logger.info("Página {} creada y validada exitosamente", clasePageObject.getSimpleName());
                return nuevaPagina;
            } else {
                logger.warn("Página {} creada pero falló validación", clasePageObject.getSimpleName());
                return nuevaPagina; // Retornar de todos modos, la validación es informativa
            }

        } catch (Exception e) {
            logger.error("Error creando página {}: {}", clasePageObject.getSimpleName(), e.getMessage());
            throw new RuntimeException("No se pudo crear la página: " + clasePageObject.getSimpleName(), e);
        }
    }

    /**
     * Obtiene una página existente del cache o crea una nueva
     *
     * @param clasePageObject Clase del Page Object
     * @return Instancia de la página
     */
    public <T extends BasePage> T obtenerPagina(Class<T> clasePageObject) {
        return crearPagina(clasePageObject);
    }

    /**
     * Crea una página específica - LoginPage
     */
    public LoginPage crearLoginPage() {
        return crearPagina(LoginPage.class);
    }

    /**
     * Crea una página específica - HomePage
     */
    public HomePage crearHomePage() {
        return crearPagina(HomePage.class);
    }

    /**
     * Crea una página específica por nombre de clase (string)
     * Útil para configuraciones dinámicas
     */
    @SuppressWarnings("unchecked")
    public <T extends BasePage> T crearPaginaPorNombre(String nombreClase) {
        try {
            Class<T> clasePageObject = (Class<T>) Class.forName(nombreClase);
            return crearPagina(clasePageObject);
        } catch (ClassNotFoundException e) {
            logger.error("Clase de página no encontrada: {}", nombreClase);
            throw new RuntimeException("Clase de página no encontrada: " + nombreClase, e);
        }
    }

    /**
     * Limpia el cache de páginas
     */
    public void limpiarCache() {
        logger.info("Limpiando cache de páginas. Páginas en cache: {}", cachePages.size());
        cachePages.clear();
    }

    /**
     * Habilita o deshabilita el cache de páginas
     */
    public void configurarCache(boolean habilitado) {
        logger.info("Cache de páginas configurado: {}", habilitado);
        this.cacheHabilitado = habilitado;

        if (!habilitado) {
            limpiarCache();
        }
    }

    /**
     * Obtiene el estado del cache
     */
    public boolean esCacheHabilitado() {
        return cacheHabilitado;
    }

    /**
     * Obtiene el número de páginas en cache
     */
    public int obtenerTamanioCache() {
        return cachePages.size();
    }

    /**
     * Remueve una página específica del cache
     */
    public void removerDelCache(Class<? extends BasePage> clasePageObject) {
        if (cachePages.remove(clasePageObject) != null) {
            logger.debug("Página {} removida del cache", clasePageObject.getSimpleName());
        }
    }

    /**
     * Verifica si una página está en cache
     */
    public boolean estaEnCache(Class<? extends BasePage> clasePageObject) {
        return cachePages.containsKey(clasePageObject);
    }

    /**
     * Builder para creación fluida de páginas con configuraciones
     */
    public static class ConstructorPagina {
        private final PageFactory factory;
        private boolean validarAlCrear = true;
        private boolean usarCache = true;

        public ConstructorPagina() {
            this.factory = PageFactory.getInstancia();
        }

        public ConstructorPagina conValidacion(boolean validar) {
            this.validarAlCrear = validar;
            return this;
        }

        public ConstructorPagina conCache(boolean cache) {
            this.usarCache = cache;
            return this;
        }

        public <T extends BasePage> T construir(Class<T> clasePageObject) {
            // Configurar temporalmente el cache según la preferencia
            boolean cacheOriginal = factory.esCacheHabilitado();
            factory.configurarCache(usarCache);

            try {
                T pagina = factory.crearPagina(clasePageObject);

                if (validarAlCrear && !pagina.validarPagina()) {
                    logger.warn("Advertencia: Página {} no pasó validación", clasePageObject.getSimpleName());
                }

                return pagina;

            } finally {
                // Restaurar configuración original del cache
                factory.configurarCache(cacheOriginal);
            }
        }
    }

    /**
     * Crea un nuevo ConstructorPagina para construcción fluida
     */
    public static ConstructorPagina constructor() {
        return new ConstructorPagina();
    }
}
package com.roberto.automatizacion.paginas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Fábrica para la creación y gestión de Page Objects
 * Implementa patrones: Factory, Singleton, Cache
 * Principios SOLID aplicados: SRP, OCP, DIP
 *
 * @author Roberto Rivas Lopez
 */
public final class FabricaPaginas {

    private static final Logger logger = LoggerFactory.getLogger(FabricaPaginas.class);
    private static volatile FabricaPaginas instancia;

    // Cache de páginas para reutilización
    private final Map<Class<? extends PaginaBase>, PaginaBase> cachePaginas = new HashMap<>();

    // Flag para habilitar/deshabilitar cache
    private boolean cacheHabilitado = true;

    private FabricaPaginas() {
        logger.info("FabricaPaginas inicializada");
    }

    /**
     * Obtiene la instancia única de la FabricaPaginas (Singleton thread-safe)
     */
    public static FabricaPaginas getInstancia() {
        if (instancia == null) {
            synchronized (FabricaPaginas.class) {
                if (instancia == null) {
                    instancia = new FabricaPaginas();
                }
            }
        }
        return instancia;
    }

    /**
     * Crea una nueva instancia de página del tipo especificado
     *
     * @param clasePaginaObjeto Clase del Page Object a crear
     * @return Nueva instancia de la página
     */
    @SuppressWarnings("unchecked")
    public <T extends PaginaBase> T crearPagina(Class<T> clasePaginaObjeto) {
        try {
            logger.debug("Creando página: {}", clasePaginaObjeto.getSimpleName());

            // Si el cache está habilitado, intentar obtener de cache primero
            if (cacheHabilitado && cachePaginas.containsKey(clasePaginaObjeto)) {
                T paginaCacheada = (T) cachePaginas.get(clasePaginaObjeto);
                logger.debug("Página obtenida del cache: {}", clasePaginaObjeto.getSimpleName());
                return paginaCacheada;
            }

            // Crear nueva instancia usando reflexión
            T nuevaPagina = clasePaginaObjeto.getDeclaredConstructor().newInstance();

            // Validar que la página se creó correctamente
            if (nuevaPagina.validarPagina()) {
                // Agregar al cache si está habilitado
                if (cacheHabilitado) {
                    cachePaginas.put(clasePaginaObjeto, nuevaPagina);
                    logger.debug("Página agregada al cache: {}", clasePaginaObjeto.getSimpleName());
                }

                logger.info("Página {} creada y validada exitosamente", clasePaginaObjeto.getSimpleName());
                return nuevaPagina;
            } else {
                logger.warn("Página {} creada pero falló validación", clasePaginaObjeto.getSimpleName());
                return nuevaPagina; // Retornar de todos modos, la validación es informativa
            }

        } catch (Exception e) {
            logger.error("Error creando página {}: {}", clasePaginaObjeto.getSimpleName(), e.getMessage());
            throw new RuntimeException("No se pudo crear la página: " + clasePaginaObjeto.getSimpleName(), e);
        }
    }

    /**
     * Obtiene una página existente del cache o crea una nueva
     *
     * @param clasePaginaObjeto Clase del Page Object
     * @return Instancia de la página
     */
    public <T extends PaginaBase> T obtenerPagina(Class<T> clasePaginaObjeto) {
        return crearPagina(clasePaginaObjeto);
    }

    /**
     * Crea una página específica - PaginaLogin
     */
    public PaginaLogin crearPaginaLogin() {
        return crearPagina(PaginaLogin.class);
    }

    /**
     * Crea una página específica - PaginaInicio
     */
    public PaginaInicio crearPaginaInicio() {
        return crearPagina(PaginaInicio.class);
    }

    /**
     * Crea una página específica por nombre de clase (string)
     * Útil para configuraciones dinámicas
     */
    @SuppressWarnings("unchecked")
    public <T extends PaginaBase> T crearPaginaPorNombre(String nombreClase) {
        try {
            Class<T> clasePaginaObjeto = (Class<T>) Class.forName(nombreClase);
            return crearPagina(clasePaginaObjeto);
        } catch (ClassNotFoundException e) {
            logger.error("Clase de página no encontrada: {}", nombreClase);
            throw new RuntimeException("Clase de página no encontrada: " + nombreClase, e);
        }
    }

    /**
     * Limpia el cache de páginas
     */
    public void limpiarCache() {
        logger.info("Limpiando cache de páginas. Páginas en cache: {}", cachePaginas.size());
        cachePaginas.clear();
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
        return cachePaginas.size();
    }

    /**
     * Remueve una página específica del cache
     */
    public void removerDelCache(Class<? extends PaginaBase> clasePaginaObjeto) {
        if (cachePaginas.remove(clasePaginaObjeto) != null) {
            logger.debug("Página {} removida del cache", clasePaginaObjeto.getSimpleName());
        }
    }

    /**
     * Verifica si una página está en cache
     */
    public boolean estaEnCache(Class<? extends PaginaBase> clasePaginaObjeto) {
        return cachePaginas.containsKey(clasePaginaObjeto);
    }

    /**
     * Constructor para creación fluida de páginas con configuraciones
     */
    public static class ConstructorPagina {
        private final FabricaPaginas fabrica;
        private boolean validarAlCrear = true;
        private boolean usarCache = true;

        public ConstructorPagina() {
            this.fabrica = FabricaPaginas.getInstancia();
        }

        public ConstructorPagina conValidacion(boolean validar) {
            this.validarAlCrear = validar;
            return this;
        }

        public ConstructorPagina conCache(boolean cache) {
            this.usarCache = cache;
            return this;
        }

        public <T extends PaginaBase> T construir(Class<T> clasePaginaObjeto) {
            // Configurar temporalmente el cache según la preferencia
            boolean cacheOriginal = fabrica.esCacheHabilitado();
            fabrica.configurarCache(usarCache);

            try {
                T pagina = fabrica.crearPagina(clasePaginaObjeto);

                if (validarAlCrear && !pagina.validarPagina()) {
                    logger.warn("Advertencia: Página {} no pasó validación", clasePaginaObjeto.getSimpleName());
                }

                return pagina;

            } finally {
                // Restaurar configuración original del cache
                fabrica.configurarCache(cacheOriginal);
            }
        }
    }

    /**
     * Crea un nuevo ConstructorPagina para construcción fluida
     */
    public static ConstructorPagina constructor() {
        return new ConstructorPagina();
    }

    // ========================================
    // MÉTODOS DE UTILIDAD Y ESTADÍSTICAS
    // ========================================

    /**
     * Obtiene estadísticas del cache de páginas
     */
    public Map<String, Object> obtenerEstadisticasCache() {
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("cacheHabilitado", cacheHabilitado);
        estadisticas.put("tamanioCache", cachePaginas.size());
        estadisticas.put("tiposPaginasEnCache", cachePaginas.keySet().stream()
                .map(Class::getSimpleName)
                .toList());

        logger.debug("Estadísticas del cache: {}", estadisticas);
        return estadisticas;
    }

    /**
     * Invalida y recrea una página específica en el cache
     */
    public <T extends PaginaBase> T invalidarYRecrearPagina(Class<T> clasePaginaObjeto) {
        logger.info("Invalidando y recreando página: {}", clasePaginaObjeto.getSimpleName());

        // Remover del cache si existe
        removerDelCache(clasePaginaObjeto);

        // Crear nueva instancia
        return crearPagina(clasePaginaObjeto);
    }

    /**
     * Valida todas las páginas en el cache
     */
    public Map<String, Boolean> validarTodasLasPaginasEnCache() {
        logger.info("Validando todas las páginas en cache");

        Map<String, Boolean> resultadosValidacion = new HashMap<>();

        for (Map.Entry<Class<? extends PaginaBase>, PaginaBase> entrada : cachePaginas.entrySet()) {
            String nombreClase = entrada.getKey().getSimpleName();
            PaginaBase pagina = entrada.getValue();

            try {
                boolean esValida = pagina.validarPagina();
                resultadosValidacion.put(nombreClase, esValida);
                logger.debug("Validación de {} en cache: {}", nombreClase, esValida);
            } catch (Exception e) {
                logger.warn("Error validando {} en cache: {}", nombreClase, e.getMessage());
                resultadosValidacion.put(nombreClase, false);
            }
        }

        logger.info("Validación de cache completada: {}", resultadosValidacion);
        return resultadosValidacion;
    }

    /**
     * Limpia páginas inválidas del cache
     */
    public int limpiarPaginasInvalidasDelCache() {
        logger.info("Limpiando páginas inválidas del cache");

        Map<String, Boolean> validaciones = validarTodasLasPaginasEnCache();
        int paginasRemovidas = 0;

        for (Map.Entry<String, Boolean> validacion : validaciones.entrySet()) {
            if (!validacion.getValue()) {
                // Encontrar la clase correspondiente y removerla
                cachePaginas.entrySet().removeIf(entrada -> {
                    boolean debeRemover = entrada.getKey().getSimpleName().equals(validacion.getKey());
                    if (debeRemover) {
                        logger.debug("Removiendo página inválida del cache: {}", validacion.getKey());
                    }
                    return debeRemover;
                });
                paginasRemovidas++;
            }
        }

        logger.info("Páginas inválidas removidas del cache: {}", paginasRemovidas);
        return paginasRemovidas;
    }

    // ========================================
    // MÉTODOS DE CONFIGURACIÓN AVANZADA
    // ========================================

    /**
     * Precarga páginas comunes en el cache
     */
    public void precargarPaginasComunes() {
        logger.info("Precargando páginas comunes en cache");

        try {
            // Precargar páginas más utilizadas
            crearPaginaLogin();
            logger.debug("PaginaLogin precargada");

            crearPaginaInicio();
            logger.debug("PaginaInicio precargada");

            logger.info("Precarga de páginas completada. Cache actual: {}", cachePaginas.size());

        } catch (Exception e) {
            logger.error("Error durante precarga de páginas: {}", e.getMessage());
        }
    }

    /**
     * Configura el comportamiento de la fábrica
     */
    public void configurarComportamiento(boolean habilitarCache, boolean precargarPaginas) {
        logger.info("Configurando comportamiento de FabricaPaginas - Cache: {}, Precarga: {}",
                habilitarCache, precargarPaginas);

        configurarCache(habilitarCache);

        if (precargarPaginas && habilitarCache) {
            precargarPaginasComunes();
        }
    }

    /**
     * Obtiene información completa del estado de la fábrica
     */
    public Map<String, Object> obtenerInformacionCompleta() {
        Map<String, Object> informacion = new HashMap<>();

        informacion.put("instanciaActiva", instancia != null);
        informacion.put("estadisticasCache", obtenerEstadisticasCache());
        informacion.put("timestampCreacion", System.currentTimeMillis());

        logger.debug("Información completa de FabricaPaginas generada");
        return informacion;
    }
}
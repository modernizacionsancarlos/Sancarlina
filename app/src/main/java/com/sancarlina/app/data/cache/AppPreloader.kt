package com.sancarlina.app.data.cache

import android.content.Context
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.di.AppContainer
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio de calentamiento y precarga global de la aplicación.
 * Calienta sólo el contenido crítico y una cantidad acotada de imágenes visibles.
 */
object AppPreloader {
    @Volatile
    private var isPreloaded = false

    suspend fun preloadAll(context: Context, container: AppContainer) = withContext(Dispatchers.IO) {
        if (isPreloaded) return@withContext
        try {
            Logger.d("AppPreloader: Iniciando pre-carga de datos e imágenes...")

            // Los repositorios aplican caché persistente, TTL y single-flight: esto no duplica
            // consultas aunque el Splash solicite los mismos datos al mismo tiempo.
            val tenants = container.tenantsRepository.getActiveTenants()
            val areas = container.areasRepository.getAreas()

            Logger.d("AppPreloader: Datos cargados (${tenants.size} comercios, ${areas.size} zonas)")

            // 2. Extraer URLs de imágenes para pre-cachear en Coil
            val imageUrls = tenants.asSequence()
                .map { it.displayImageUrl() }
                .filter { it.isNotBlank() }
                .distinct()
                .take(MAX_PRELOADED_IMAGES)
                .toList()

            // Sólo las primeras tarjetas: las galerías se descargan cuando el usuario abre el detalle.
            val imageLoader = context.imageLoader
            imageUrls.forEach { url ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .size(640, 360)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()
                imageLoader.enqueue(request)
            }

            // Si no hubo catálogo (por ejemplo, primera apertura sin red), se reintenta en el
            // próximo foreground en vez de marcar una precarga vacía como terminada.
            isPreloaded = tenants.isNotEmpty()
            Logger.d("AppPreloader: ${minOf(imageUrls.size, MAX_PRELOADED_IMAGES)} imágenes críticas en precarga")
        } catch (e: Exception) {
            Logger.e("AppPreloader: Error durante pre-carga", e)
        }
    }

    private const val MAX_PRELOADED_IMAGES = 6
}

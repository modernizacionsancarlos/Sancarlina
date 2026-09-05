package com.sancarlina.app.data.cache

import android.content.Context
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.sancarlina.app.data.models.displayImageUrl
import com.sancarlina.app.di.AppContainer
import com.sancarlina.app.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Servicio de calentamiento y precarga global de la aplicación.
 * Garantiza que toda la información e imágenes clave estén en caché al iniciar la app.
 */
object AppPreloader {
    @Volatile
    private var isPreloaded = false

    suspend fun preloadAll(context: Context, container: AppContainer) = withContext(Dispatchers.IO) {
        if (isPreloaded) return@withContext
        try {
            Logger.d("AppPreloader: Iniciando pre-carga de datos e imágenes...")

            // 1. Pre-cargar datos en paralelo
            val tenantsDeferred = async { container.tenantsRepository.getActiveTenants() }
            val areasDeferred = async { container.areasRepository.getAreas() }
            val benefitsDeferred = async { container.benefitsRepository.getActiveBenefits() }

            val tenants = tenantsDeferred.await()
            val areas = areasDeferred.await()
            val benefits = benefitsDeferred.await()

            Logger.d("AppPreloader: Datos cargados (${tenants.size} comercios, ${areas.size} zonas, ${benefits.size} beneficios)")

            // 2. Extraer URLs de imágenes para pre-cachear en Coil
            val imageUrls = mutableSetOf<String>()

            tenants.forEach { tenant ->
                val displayUrl = tenant.displayImageUrl()
                if (displayUrl.isNotBlank()) imageUrls.add(displayUrl)
                if (tenant.logoUrl.isNotBlank()) imageUrls.add(tenant.logoUrl)
                tenant.gallery.filter { it.isNotBlank() }.forEach { imageUrls.add(it) }
            }

            benefits.forEach { benefit ->
                if (benefit.cover_url.isNotBlank()) imageUrls.add(benefit.cover_url)
            }

            // 3. Pre-cargar en Coil (memoria y disco)
            val imageLoader = context.imageLoader
            coroutineScope {
                imageUrls.take(50).forEach { url ->
                    launch {
                        try {
                            val request = ImageRequest.Builder(context)
                                .data(url)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .build()
                            imageLoader.enqueue(request)
                        } catch (_: Exception) {
                            // Error de carga individual no interrumpe el resto
                        }
                    }
                }
            }

            isPreloaded = true
            Logger.d("AppPreloader: Pre-carga de imágenes iniciada (${imageUrls.size} recursos procesados)")
        } catch (e: Exception) {
            Logger.e("AppPreloader: Error durante pre-carga", e)
        }
    }
}

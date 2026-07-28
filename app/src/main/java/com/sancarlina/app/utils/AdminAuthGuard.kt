package com.sancarlina.app.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sancarlina.app.data.remote.FirestoreCollections
import kotlinx.coroutines.tasks.await

object AdminAuthGuard {

    /**
     * Valida rigurosamente si el usuario autenticado actual es un SuperAdmin activo.
     * 1. Verifica token con refresh forzado (getIdToken(true)) para actualizar claims.
     * 2. Consulta la colección `superAdmins/{uid}` verificando que exista y `active != false`.
     */
    suspend fun verifyAdminAccess(
        auth: FirebaseAuth = FirebaseAuth.getInstance(),
        firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    ): Result<Boolean> {
        val user = auth.currentUser
            ?: return Result.failure(SecurityException("No hay sesión activa"))

        return try {
            // Forzar actualización del token para traer claims actualizadas desde Firebase Auth server
            val tokenResult = user.getIdToken(true).await()
            val claims = tokenResult.claims

            val isRoleAdmin = claims["role"] == "admin"
            val isSuperAdminClaim = claims["superAdmin"] == true || claims["admin"] == true

            // Consultar documento superAdmins/{uid}
            val superAdminDoc = firestore.collection(FirestoreCollections.SUPER_ADMINS)
                .document(user.uid)
                .get()
                .await()

            if (!superAdminDoc.exists()) {
                return Result.failure(SecurityException("Usuario no registrado en superAdmins"))
            }

            val isActive = superAdminDoc.getBoolean("active") ?: true
            if (!isActive) {
                return Result.failure(SecurityException("Cuenta de superAdmin desactivada"))
            }

            if (isRoleAdmin || isSuperAdminClaim || superAdminDoc.exists()) {
                Result.success(true)
            } else {
                Result.failure(SecurityException("Rol insuficiente para acceso administrativo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

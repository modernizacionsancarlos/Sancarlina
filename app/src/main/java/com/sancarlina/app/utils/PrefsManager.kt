package com.sancarlina.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PrefsManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_sancarlina_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val oldPrefs: SharedPreferences = context.getSharedPreferences("sancarlina_prefs", Context.MODE_PRIVATE)

    init {
        migrateIfNeeded()
    }

    private fun migrateIfNeeded() {
        if (oldPrefs.all.isNotEmpty()) {
            val editor = encryptedPrefs.edit()
            oldPrefs.all.forEach { (key, value) ->
                when (value) {
                    is String -> editor.putString(key, value)
                    is Boolean -> editor.putBoolean(key, value)
                    is Int -> editor.putInt(key, value)
                    is Float -> editor.putFloat(key, value)
                    is Long -> editor.putLong(key, value)
                }
            }
            editor.apply()
            oldPrefs.edit().clear().apply()
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        encryptedPrefs.edit().putBoolean("onboarding_completed", completed).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return encryptedPrefs.getBoolean("onboarding_completed", false)
    }

    fun setGuideCompleted(completed: Boolean) {
        encryptedPrefs.edit().putBoolean("guide_completed", completed).apply()
    }

    fun isGuideCompleted(): Boolean {
        return encryptedPrefs.getBoolean("guide_completed", false)
    }
    
    // Add more methods for other sensitive data as needed
}

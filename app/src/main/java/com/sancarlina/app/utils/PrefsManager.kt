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
    private val legacyGuidePrefs: SharedPreferences = context.getSharedPreferences("gondolapp_prefs", Context.MODE_PRIVATE)

    init {
        migrateIfNeeded()
        migrateLegacyGuideIfNeeded()
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

    private fun migrateLegacyGuideIfNeeded() {
        if (
            !encryptedPrefs.contains(KEY_ONBOARDING_COMPLETED) &&
            legacyGuidePrefs.getBoolean(KEY_LEGACY_COACHMARKS_SEEN, false)
        ) {
            encryptedPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        encryptedPrefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return encryptedPrefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setGuideCompleted(completed: Boolean) {
        encryptedPrefs.edit().putBoolean("guide_completed", completed).apply()
    }

    fun isGuideCompleted(): Boolean {
        return encryptedPrefs.getBoolean("guide_completed", false)
    }

    fun setAppTheme(theme: com.sancarlina.app.ui.theme.AppTheme) {
        encryptedPrefs.edit().putString(KEY_APP_THEME, theme.storageKey).apply()
    }

    fun getAppTheme(): com.sancarlina.app.ui.theme.AppTheme {
        val key = encryptedPrefs.getString(KEY_APP_THEME, com.sancarlina.app.ui.theme.AppTheme.LIGHT.storageKey)
        return com.sancarlina.app.ui.theme.AppTheme.fromKey(key)
    }

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        const val KEY_LEGACY_COACHMARKS_SEEN = "seen_coachmarks"
        const val KEY_APP_THEME = "app_theme_mode"
    }
}

package com.sancarlina.app.ui.theme

enum class AppTheme(val storageKey: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        fun fromKey(key: String?): AppTheme {
            return when (key) {
                DARK.storageKey -> DARK
                SYSTEM.storageKey -> SYSTEM
                else -> LIGHT // El modo claro es el predeterminado
            }
        }
    }
}

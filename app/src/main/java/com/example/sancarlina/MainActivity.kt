package com.example.sancarlina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sancarlina.ui.components.MainScaffold
import com.example.sancarlina.ui.theme.SancarlinaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilita el diseño de borde a borde (edge-to-edge)
        enableEdgeToEdge()
        
        setContent {
            // Aplicación del Tema Oficial de Sancarlina
            SancarlinaTheme {
                // Entrada principal al Scaffold que contiene la navegación y el contenido central
                MainScaffold()
            }
        }
    }
}

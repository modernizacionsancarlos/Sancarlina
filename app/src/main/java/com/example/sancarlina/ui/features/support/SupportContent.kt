package com.example.sancarlina.ui.features.support

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportContent(onBack: () -> Unit, onOpenDrawer: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "GÓNDOLA SANCARLINA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SancarlinaPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SancarlinaBackground)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar en ayuda...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "Preguntas Frecuentes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            FaqItem(
                question = "¿Cómo funciona el sistema de puntos?",
                answer = "El sistema de puntos de Sancarlina te recompensa por cada compra directa a productores locales. Acumulás 1 punto por cada $100 de compra, que luego podés canjear por descuentos en futuros pedidos o productos exclusivos del catálogo de origen."
            )

            Spacer(modifier = Modifier.height(12.dp))

            FaqItem(
                question = "¿Cómo me contacto con un productor?",
                answer = "Para contactar a un productor, ingresá al perfil del mismo desde el catálogo y seleccioná el botón \"Mensaje\". También podés dejar consultas públicas en la sección de reseñas de cada producto para que la comunidad y el productor puedan responder."
            )

            Spacer(modifier = Modifier.height(12.dp))

            FaqItem(
                question = "¿Qué requisitos necesito para el Sello de Origen?",
                answer = "El Sello de Origen requiere que al menos el 80% de la materia prima sea de la región de San Carlos, certificar prácticas sostenibles de cultivo o producción, y estar registrado formalmente como productor local en nuestra plataforma tras una verificación en terreno."
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Support Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SancarlinaPrimary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SancarlinaPrimary.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "¿Todavía tenés dudas?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Nuestro equipo de soporte técnico está disponible de lunes a viernes de 9 a 18hs para ayudarte con cualquier inconveniente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    Button(
                        onClick = { /* TODO: WhatsApp Support */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Forum, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CHATEAR CON SOPORTE TÉCNICO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 12.dp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

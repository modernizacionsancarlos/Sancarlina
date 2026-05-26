package com.example.sancarlina.ui.features.servicios

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sancarlina.ui.theme.SancarlinaAccent
import com.example.sancarlina.ui.theme.SancarlinaBackground
import com.example.sancarlina.ui.theme.SancarlinaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosSelloContent(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SERVICIOS Y SELLO",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
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
        ) {
            // Header Image (Vista 3 Style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDBlnBzECUfV3Q8L9JVdnEfGXXRXrm9ZfJJ0m6tqX_9A7EchP_mYE96WOuNQsAK2rDez5ls2ZSOBgbO_O0Q-6C8z2mPntKysTbXVeoEAWu5DOP0ALbMWzGu0HhWe34w0RAPmCyOD9lsSuyWoHUOd6FY_Q3uuFxqXkSrmx7q9kczsPQcXY4f4gCsMlepsiSSQILCTM50_OL5UBRHHhVQhXNLGqBWD0GcjVj0skE_S3r06yEUjpBUgKDMA4b2McArh82DvsEZb-1EjWs",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SancarlinaPrimary.copy(alpha = 0.7f))
                )
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Sello de Origen",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Garantía de calidad sancarlina",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Categories Grid (Informative)
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Nuestros Servicios",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SancarlinaPrimary
                )
                Spacer(Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ServiceItem("Miel", "https://lh3.googleusercontent.com/aida-public/AB6AXuBO-zCbcsNCC2scnOIN4pWhL6y4EXC8DB8kozNHGOfapsQSChWI9tpAqFHzamfrcaRTLQ1luw689jxdB5Qd6T4BiQxmi2udVClm4tFOQFdVlwZp5Nv8G4RieGgZ4PDU624OHl0-ymlL7qzjgg7CVcxXDeMNVA2OuGjLfnP6K-5iG7xmLQoWV_21QUqXu9YSIAfqnuauPRmukhPqmz4uk9IHWTqoiPjEQR7qijlzg9mb0QKOcMnB9UJCIPCSLS3pC-_w4ev74l_5BSo")
                    ServiceItem("Conservas", "https://lh3.googleusercontent.com/aida-public/AB6AXuC-JzLy0T0x03mi-Dz-tDSBj8W7Z6IWSd_vTaXOkRXefQq1BuB86rhJnoWcp7hrUP17h1gtJSjI66HJadhtPGYKDSStAE6fJSBf2CfeCJ5xlcB6knY6SCRBu-hIYE23ti1-cGSbhGhrGlzVfFmOCpHqkfKLeVBc45LiSzu7QzX2J0rIurfrnAUAO8bXXE5AubTq6vW8q7GgZA1XbbuKLh8CdWP9Zfdhw1-k-pJ6FGSQKH0slKRMpNXHUwGbbC2B8zW7wiBTg1LZ3_w")
                    ServiceItem("Vinos", "https://lh3.googleusercontent.com/aida-public/AB6AXuAaRawZWDnHG1z12w270gKPsi8Tnj5iO0HexCUOQ2_1tCMa-3BFniZRFsj6IBsEmWkTGFUp0rH3M11Hwg6rzLLtBAWwEkrSFMFNPm8ydbMSwfzTolzPR96AZPowv5gZNIommIAbQm-7xsi-LhI9BtSsZD6xqiFF4-2tChqUGsXhitwykVv9pvI8mjhz03qxss3E-hmj8Ggcyct33e2zFpiIVA7jBfqEszqSZewoq2M41MvFuyTvkEXLPHKEQ6w96owDshdzu5UYd_w")
                }
            }

            Spacer(Modifier.height(32.dp))

            // Sello de Origen Detailed Section
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = Color.White,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = SancarlinaAccent,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Hecho en San Carlos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        "El Sello de Origen es una certificación de la Municipalidad que garantiza que el producto que estás adquiriendo ha sido elaborado íntegramente en nuestro departamento, respetando procesos artesanales y estándares de calidad premium.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )
                    
                    Spacer(Modifier.height(24.dp))
                    
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuB2uPoSiPE1XyG8NAP1f9MSv0310aepTL_IGeK3YZSw4_Lq-dnj8fiwRJtfIqRNWvvvXTxMrX5QcQwzzRFtfWmKXW9WV_A5ad8W-BxsLTC83Z3iHgUgHdYu0UONKlSdNFYbPZ1B8fmOLGdHel3kDytd7o89Labc2EDn6vNX4S4fhRx4kWBCvb3Mq0hgj2Sj5AtC6zt8eb_XiDJF5VYfoE4kbLne2Ugnnh_4CPB0ujd25qG00kezuR3luyN-bwbXUnsIorvi2zvYLg4",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ServiceItem(name: String, imageUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF3F5E4),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E4D3))
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

package com.example.sancarlina.ui.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sancarlina.R
import com.example.sancarlina.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int? = null,
    val imageUrl: String? = null
)

@Composable
fun OnboardingContent(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Tu ciudad, en tus manos.",
            description = "Descubre todo lo que San Carlos tiene para ofrecerte.",
            imageRes = R.drawable.isotipo 
        ),
        OnboardingPage(
            title = "Explora lo Local.",
            description = "Encuentra bodegas, emprendedores y servicios regionales con nuestro mapa interactivo.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDR7fJTZFE8JC-jbBEEXqwKx4eQ9tKdpfStvBK4mTzoPzMk0SJns9EG6SD50Mpoz0XKOlTnz_LadQNg8nhvGMSodQbdkC-khhlnmMkF3u9kp6xdpW5HjOXWosku-khh2gMgGNb6yx_9GPoSx6beIl-Yro2pCcHCEJMlhC8lC8t-8ndz0YyOIGm3QQtk7lINPBoRkS7GdlzDmsbY9X-jeoGyk_tWogB_1aYLu-nrGO1KxCBm8HaaKV60PyGv5GuOh3RmDiS24P2qnyQ"
        ),
        OnboardingPage(
            title = "Suma y Gana.",
            description = "Escanea códigos QR en tus compras, acumula puntos y canjealos por beneficios exclusivos.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDskGhaHii9PH9sY5bjkC4-T66SVNhSYiT4yxb60L7qIBeBYjFp14RfXHZkiFTpnRtg18PzuIpBIvevcyeZNnOUh5ZUmfPWRT511xLs0kNTrPqRLQyYgncm8m1OtdjWqO-yie4C4WDZ6G9tN84ZNIHALKnjkAY7zDoWiIlInh6eVITmN6Tlpt_EmayyH5u4gK9vdrotuzbPLaqOcTNGDEpE93glq25DwvdbI8sMWJahyBfcXMMmKt20H5ZzJzpgRbjf0r5GdtYsK_I"
        ),
        OnboardingPage(
            title = "Únete a la Red.",
            description = "Registrate para guardar tus favoritos y ser parte del crecimiento de nuestra región.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB5sExiGGPv7NLQVXTLBoriVswbU2UPNsyWy5QgimSOKrSDYnzvoMDyGVl1bWAcQrMbnBXcylJYRK36bltho-1rrBlAutupNAp5UVpH3T1jACCf6MDh9ejJ20YmXxttGF9qYVNIawUqVPUM_NkT5diNRVWfDGMfKuvU2QWMxI-UQSv19w4zz56LLnr5Xih6pwBsX3Nzmpzy2oL8Epwn_kySf4xftaR4prbfEwIjJbT1GogLEjHAvI8ts74ANkQzgM0_DdCw-8FQ3lg"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SancarlinaBackground)
    ) {
        // Decorative background blur elements
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 200.dp, y = (-100).dp)
                .background(SancarlinaPrimary.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-100).dp, y = 600.dp)
                .background(SancarlinaAccent.copy(alpha = 0.05f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                val page = pages[index]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (index == 0) {
                        // First page has the logo and name
                        Image(
                            painter = painterResource(id = R.drawable.isotipo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(180.dp)
                                .padding(bottom = 16.dp)
                        )
                        Text(
                            text = "SANCARLINA",
                            style = MaterialTheme.typography.displaySmall,
                            color = SancarlinaAccent,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            modifier = Modifier.padding(bottom = 32.dp)
                        )
                    } else if (page.imageRes != null) {
                        Image(
                            painter = painterResource(id = page.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(280.dp)
                                .padding(bottom = 32.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else if (page.imageUrl != null) {
                        AsyncImage(
                            model = page.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White)
                                .padding(bottom = 32.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineLarge,
                        color = SancarlinaPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(280.dp)
                    )
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page Indicator
                Row(
                    modifier = Modifier.height(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) SancarlinaAccent else Color.LightGray
                        val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color)
                                .size(width = width, height = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SancarlinaAccent),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "COMENZAR" else "SIGUIENTE",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onFinish) {
                    Text(
                        text = "SALTAR",
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

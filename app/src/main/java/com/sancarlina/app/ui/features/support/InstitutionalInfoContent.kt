package com.sancarlina.app.ui.features.support

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssuredWorkload
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sancarlina.app.R
import com.sancarlina.app.ui.components.SancarlinaCard
import com.sancarlina.app.ui.components.SancarlinaTopBar
import com.sancarlina.app.ui.theme.*
import com.sancarlina.app.utils.BrowserUtils

@Composable
fun InstitutionalInfoContent(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SancarlinaTopBar(
            title = stringResource(R.string.institutional_title),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Logo & Title
            Box(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow behind logo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), CircleShape)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_sancarlina_logo),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }

            Text(
                text = "Municipalidad de San Carlos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.institutional_modernizacion),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Misión Bento Card
            SancarlinaCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        modifier = Modifier.size(44.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AssuredWorkload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.institutional_mision_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.institutional_mision_body),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.15f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Team Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.institutional_team_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Team Members Grid (2x2)
            val team = listOf(
                TeamMember(
                    name = "Valeria Ruiz",
                    role = "Directora",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBLi5u9Hu9P8Yh6fCjpKXIy96qJZPzLTxWdJlYXCvTPoSBM3O5eSWoYzSi2cbU015mpqbI-g0I77WN64iaLYWwKR_Ia_6MifWgQ-nvqMKb2_HiIx2kgVgQsQD1OgMEwCK5yfrqxT-7TWhFOcDNgfqWDTankpwImEzL-wAx9UqqjGK7ZIalehIuljpZJagKD9CPMIB5HDk729Cvr9yylW_abfZLUWxd8Mi01XRNKxqyyoiOh4ShWCvEJf0TjwQ7Y0QUgC43KDIUth7Z3",
                    initials = "VR"
                ),
                TeamMember(
                    name = "Martín Silva",
                    role = "Líder de Proyectos",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDMw3sRrl4FmtUabZnYM1C7nNOtrk8r38eNzbwqflLl8keH_BKiVEcHyQjXGLGxEVHAds29RQVsAAlZvFGyvaYPVjB2sB-bEdfws_lWyhK9kf_IM67pUKyTSturmugzWBfSwAhrDfkI8fAkZrzWk07UT3WKdlmmI9Kn1_qCOoqtEI1207d1KlVXuMEfTJXPOP6wU5zwIW8MuUfhvH6ivMDnsyouO9Xtg0AFwf512LCIMf3ySmoijnZemrlLq_NTmQvtjVaGZLxKyUG6",
                    initials = "MS"
                ),
                TeamMember(
                    name = "Laura Gómez",
                    role = "UX/UI Design",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAY1Vt0cqXX3W-jH-JEHmmNr1s4FsfoD3k8ACs39wQalNPM0CcOwC9j6l3Jquzqwo4kUuCuJXoErpRhrdUWdGV1L1rwWK5Cg1o5ZbIg1xVb4XkFmBUXb_Z_UUngFO8_kliHJkS8nnDevz63tJFDA-iPjcbsO4y-gi3ord8fGPt4B3QdPtz2Xwp1FjLe59YqwsUzPIGPJ4eI-Q00JUbkXrgindimJ5to6AnIuRPBSC13wyrmUDxKlrKtrvt3SXwAjiRLNQNP8_Sa_UDY",
                    initials = "LG"
                ),
                TeamMember(
                    name = "Diego Vega",
                    role = "Desarrollo",
                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDxYHm10kLW8VfSMnyuVvZdHlanf_cx9UJ9hlk05GG6CbtbtXrEclqbCbtMKP7Bio4Mz8X4NLXMr7ICbfQoklwoG2E6kVPNJUhbdDNiyNh9MW8NIzzz3XGhqOefPLISoDQsShkS0TR-j8W5LxCsFcrMuZrJetoN17q82aHapuy-sEb-EX60zsmkYRrQydYnDnk2U99-Wp7IFlMQrxYr_lhh85jqeE0x-G73wW8vp4hy0m9VDzY0eeFGwaTC6qm24PnSqNstQ7Bw7F3U",
                    initials = "DV"
                )
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in team.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TeamMemberCard(member = team[i], modifier = Modifier.weight(1f))
                        if (i + 1 < team.size) {
                            TeamMemberCard(member = team[i + 1], modifier = Modifier.weight(1f))
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Social Networks Section
            Text(
                text = stringResource(R.string.institutional_connect_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                SocialIcon(
                    icon = Icons.Default.Language,
                    label = "Web",
                    onClick = { BrowserUtils.openCustomTab(context, "https://sancarlos.gob.ar") }
                )
                SocialIcon(
                    icon = Icons.Default.ThumbUp,
                    label = "Facebook",
                    onClick = { BrowserUtils.openCustomTab(context, "https://www.facebook.com/munisancarlosmza") }
                )
                SocialIcon(
                    icon = Icons.Default.PhotoCamera,
                    label = "Instagram",
                    onClick = { BrowserUtils.openCustomTab(context, "https://www.instagram.com/municipalidadsancarlos") }
                )
            }
        }
    }
}

data class TeamMember(
    val name: String,
    val role: String,
    val imageUrl: String,
    val initials: String
)

@Composable
fun TeamMemberCard(
    member: TeamMember,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = SancarlinaCardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = member.imageUrl,
                contentDescription = member.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_gallery),
                fallback = painterResource(id = android.R.drawable.ic_menu_gallery)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = member.role,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SocialIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(52.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

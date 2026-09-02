package com.sancarlina.app.ui.features.auth

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sancarlina.app.R
import com.sancarlina.app.ui.theme.*
import kotlinx.coroutines.launch

enum class OnboardingDestination {
    Home,
    Login
}

@Immutable
private data class OnboardingPage(
    val eyebrow: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val supportingIconStart: ImageVector,
    val supportingLabelStart: String,
    val supportingIconEnd: ImageVector,
    val supportingLabelEnd: String,
    val accent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color
)

@Composable
fun OnboardingContent(
    onFinish: (OnboardingDestination) -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            eyebrow = stringResource(R.string.onboarding_discover_eyebrow),
            title = stringResource(R.string.onboarding_discover_title),
            description = stringResource(R.string.onboarding_discover_description),
            icon = Icons.Default.Storefront,
            supportingIconStart = Icons.Default.Search,
            supportingLabelStart = stringResource(R.string.onboarding_discover_action_one),
            supportingIconEnd = Icons.Default.Explore,
            supportingLabelEnd = stringResource(R.string.onboarding_discover_action_two),
            accent = MaterialTheme.colorScheme.primary,
            accentContainer = MaterialTheme.colorScheme.primaryContainer,
            onAccentContainer = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        OnboardingPage(
            eyebrow = stringResource(R.string.onboarding_map_eyebrow),
            title = stringResource(R.string.onboarding_map_title),
            description = stringResource(R.string.onboarding_map_description),
            icon = Icons.Default.Map,
            supportingIconStart = Icons.Default.LocationOn,
            supportingLabelStart = stringResource(R.string.onboarding_map_action_one),
            supportingIconEnd = Icons.AutoMirrored.Filled.ArrowForward,
            supportingLabelEnd = stringResource(R.string.onboarding_map_action_two),
            accent = MaterialTheme.colorScheme.secondary,
            accentContainer = MaterialTheme.colorScheme.secondaryContainer,
            onAccentContainer = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        OnboardingPage(
            eyebrow = stringResource(R.string.onboarding_points_eyebrow),
            title = stringResource(R.string.onboarding_points_title),
            description = stringResource(R.string.onboarding_points_description),
            icon = Icons.Default.QrCodeScanner,
            supportingIconStart = Icons.Default.Stars,
            supportingLabelStart = stringResource(R.string.onboarding_points_action_one),
            supportingIconEnd = Icons.Default.CardGiftcard,
            supportingLabelEnd = stringResource(R.string.onboarding_points_action_two),
            accent = MaterialTheme.colorScheme.tertiary,
            accentContainer = MaterialTheme.colorScheme.tertiaryContainer,
            onAccentContainer = MaterialTheme.colorScheme.onTertiaryContainer
        )
    )
    val pagerState = rememberPagerState(pageCount = pages::size)
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        OnboardingHeader(
            currentPage = pagerState.currentPage,
            pageCount = pages.size,
            onSkip = { onFinish(OnboardingDestination.Home) }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("onboarding_pager")
        ) { index ->
            OnboardingPageContent(page = pages[index])
        }

        OnboardingFooter(
            currentPage = pagerState.currentPage,
            pageCount = pages.size,
            isLastPage = isLastPage,
            onPrimaryClick = {
                if (isLastPage) {
                    onFinish(OnboardingDestination.Home)
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            onLogin = { onFinish(OnboardingDestination.Login) }
        )
    }
}

@Composable
private fun OnboardingHeader(
    currentPage: Int,
    pageCount: Int,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = GondolDimens.ScreenPadding, end = 8.dp, top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.ic_sancarlina_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .width(132.dp)
                .heightIn(max = 36.dp)
        )

        TextButton(onClick = onSkip) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    val progressDescription = stringResource(
        R.string.onboarding_step_progress,
        currentPage + 1,
        pageCount
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = GondolDimens.ScreenPadding, vertical = 8.dp)
            .semantics { contentDescription = progressDescription },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (index <= currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val compact = maxHeight < 460.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = if (maxWidth >= 600.dp) GondolDimens.TabletScreenPadding else GondolDimens.ScreenPadding,
                    vertical = if (compact) 8.dp else 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingIllustration(
                page = page,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .height(if (compact) 190.dp else 250.dp)
            )

            Spacer(modifier = Modifier.height(if (compact) 16.dp else 28.dp))

            Text(
                text = page.eyebrow,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = page.accent
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 520.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 520.dp)
            )
        }
    }
}

@Composable
private fun OnboardingIllustration(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(GondolDimens.ImmersiveCardRadius),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 34.dp, y = (-38).dp)
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(page.accentContainer.copy(alpha = 0.7f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-42).dp, y = 48.dp)
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(page.accentContainer.copy(alpha = 0.5f))
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-16).dp)
                    .size(128.dp),
                shape = RoundedCornerShape(38.dp),
                color = page.accentContainer,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = page.onAccentContainer,
                        modifier = Modifier.size(66.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OnboardingCapability(
                    icon = page.supportingIconStart,
                    label = page.supportingLabelStart,
                    accent = page.accent,
                    modifier = Modifier.weight(1f)
                )
                OnboardingCapability(
                    icon = page.supportingIconEnd,
                    label = page.supportingLabelEnd,
                    accent = page.accent,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OnboardingCapability(
    icon: ImageVector,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun OnboardingFooter(
    currentPage: Int,
    pageCount: Int,
    isLastPage: Boolean,
    onPrimaryClick: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = GondolDimens.ScreenPadding, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onPrimaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .heightIn(min = 52.dp)
                .testTag("onboarding_primary_action"),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            )
        ) {
            Text(
                text = if (isLastPage) {
                    stringResource(R.string.onboarding_explore)
                } else {
                    stringResource(R.string.onboarding_next)
                },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null
            )
        }

        if (isLastPage) {
            TextButton(
                onClick = onLogin,
                modifier = Modifier.testTag("onboarding_login_action")
            ) {
                Text(
                    text = stringResource(R.string.onboarding_login),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Text(
                text = stringResource(
                    R.string.onboarding_step_progress,
                    currentPage + 1,
                    pageCount
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
}

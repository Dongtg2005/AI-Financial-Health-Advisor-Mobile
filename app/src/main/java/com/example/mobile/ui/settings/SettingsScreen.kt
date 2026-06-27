package com.example.mobile.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.BottomNav
import com.example.mobile.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Cài đặt",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W800
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = { BottomNav(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Ứng dụng",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(start = 4.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Rounded.Palette,
                            title = "Giao diện",
                            subtitle = "Sáng, Tối, Hệ thống",
                            onClick = {}
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        SettingsItem(
                            icon = Icons.Rounded.Language,
                            title = "Ngôn ngữ",
                            subtitle = "Tiếng Việt",
                            onClick = {}
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        SettingsItem(
                            icon = Icons.Rounded.Lock,
                            title = "Bảo mật",
                            subtitle = "Vân tay, PIN",
                            onClick = {}
                        )
                    }
                }

                Text(
                    "Thông tin",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(start = 4.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Rounded.Info,
                            title = "Phiên bản",
                            subtitle = "1.0.0 (Premium Gold)",
                            showChevron = false,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W700
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.W600
            )
        }

        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

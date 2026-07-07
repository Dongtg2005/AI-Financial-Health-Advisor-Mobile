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
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.BottomNav
import com.example.mobile.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController = rememberNavController()) {

    // Trang thai dialog hien tai
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // ─── Dialogs ─────────────────────────────────────────────────────────────

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            icon = { Icon(Icons.Rounded.Palette, contentDescription = null, tint = Color(0xFF1A237E)) },
            title = { Text("Giao dien", fontWeight = FontWeight.W800) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Sang (Hien tai)", "Toi (Sap ra mat)", "Theo he thong (Sap ra mat)").forEach { option ->
                        val isActive = option.contains("Hien tai")
                        ListItem(
                            headlineContent = {
                                Text(
                                    option,
                                    fontWeight = if (isActive) FontWeight.W700 else FontWeight.W500,
                                    color = if (isActive) Color(0xFF1A237E) else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            leadingContent = {
                                if (isActive) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF1A237E)
                                    )
                                } else {
                                    Spacer(Modifier.size(24.dp))
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Dong", fontWeight = FontWeight.W700, color = Color(0xFF1A237E))
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            icon = { Icon(Icons.Rounded.Language, contentDescription = null, tint = Color(0xFF1A237E)) },
            title = { Text("Ngon ngu", fontWeight = FontWeight.W800) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Tieng Viet (Hien tai)", "English (Coming soon)").forEach { lang ->
                        val isActive = lang.contains("Hien tai")
                        ListItem(
                            headlineContent = {
                                Text(
                                    lang,
                                    fontWeight = if (isActive) FontWeight.W700 else FontWeight.W500,
                                    color = if (isActive) Color(0xFF1A237E) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            },
                            leadingContent = {
                                if (isActive) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF1A237E))
                                } else {
                                    Spacer(Modifier.size(24.dp))
                                }
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Dong", fontWeight = FontWeight.W700, color = Color(0xFF1A237E))
                }
            }
        )
    }

    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            icon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color(0xFF1A237E)) },
            title = { Text("Bao mat ung dung", fontWeight = FontWeight.W800) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "He thong bao mat hien tai:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.W700,
                        color = Color(0xFF1A237E)
                    )
                    val features = listOf(
                        Icons.Rounded.Lock to "Ma hoa JWT 24 gio",
                        Icons.Rounded.Shield to "BCrypt mat khau",
                        Icons.Rounded.Storage to "pgcrypto: ma hoa thu nhap & no",
                        Icons.Rounded.Key to "EncryptedSharedPreferences tren thiet bi"
                    )
                    features.forEach { (icon, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(icon, contentDescription = null,
                                tint = Color(0xFF1A237E), modifier = Modifier.size(18.dp))
                            Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.W600)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Van tay & PIN: Sap tich hop trong phien ban tiep theo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Da hieu", fontWeight = FontWeight.W700, color = Color(0xFF1A237E))
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = { Icon(Icons.Rounded.Info, contentDescription = null, tint = Color(0xFF1A237E)) },
            title = { Text("Thong tin ung dung", fontWeight = FontWeight.W800) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "Ten ung dung" to "AI Financial Health Advisor",
                        "Phien ban" to "1.0.0",
                        "Backend" to "Spring Boot 4.0 / PostgreSQL 15",
                        "Mobile" to "Android Kotlin / Jetpack Compose",
                        "Bao mat" to "JWT + pgcrypto AES"
                    ).forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(key, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.W600)
                            Text(value, style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.W700, color = Color(0xFF1A237E))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Dong", fontWeight = FontWeight.W700, color = Color(0xFF1A237E))
                }
            }
        )
    }

    // ─── Main UI ─────────────────────────────────────────────────────────────

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Cai dat",
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
                    "Ung dung",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(start = 4.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Rounded.Palette,
                            title = "Giao dien",
                            subtitle = "Sang (Hien tai)",
                            onClick = { showThemeDialog = true }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        SettingsItem(
                            icon = Icons.Rounded.Language,
                            title = "Ngon ngu",
                            subtitle = "Tieng Viet",
                            onClick = { showLanguageDialog = true }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        SettingsItem(
                            icon = Icons.Rounded.Lock,
                            title = "Bao mat",
                            subtitle = "JWT + pgcrypto + BCrypt",
                            onClick = { showSecurityDialog = true }
                        )
                    }
                }

                Text(
                    "Thong tin",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W800,
                    modifier = Modifier.padding(start = 4.dp)
                )

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        SettingsItem(
                            icon = Icons.Rounded.Info,
                            title = "Phien ban",
                            subtitle = "1.0.0",
                            onClick = { showAboutDialog = true }
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

@Preview(showBackground = true, showSystemUi = true, name = "SettingsScreen")
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}

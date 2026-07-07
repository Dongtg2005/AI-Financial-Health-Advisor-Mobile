package com.example.mobile.ui.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mobile.data.network.dto.ScoreHistoryDTO
import com.example.mobile.ui.components.AuroraBackground
import com.example.mobile.ui.components.GlassCard
import com.example.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreHistoryScreen(
    navController: NavController,
    viewModel: ScoreHistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchScoreHistory()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuroraBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = "Lich su Diem Sức khỏe",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W800,
                            color = Color(0xFF1A237E)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Quay lai",
                                tint = Color(0xFF1A237E)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.isLoading && uiState.scores.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF1A237E))
                    }
                } else if (uiState.errorMessage != null && uiState.scores.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(uiState.errorMessage ?: "Loi tai du lieu", color = RedDanger, fontWeight = FontWeight.Bold)
                            Button(onClick = { viewModel.fetchScoreHistory() }) {
                                Text("Thu lai")
                            }
                        }
                    }
                } else {
                    // Custom Bar Chart
                    ScoreChart(scores = uiState.scores)

                    Text(
                        text = "CHI TIET DIEM THEO TUAN",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E).copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                    )

                    if (uiState.scores.isEmpty()) {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    "Chua co diem lich su. He thong se tu dong chot diem vao 00:00 chu nhat hang tuan.",
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF1A237E).copy(alpha = 0.6f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 32.dp)
                        ) {
                            items(uiState.scores, key = { it.id }) { item ->
                                ScoreHistoryItem(item = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreChart(scores: List<ScoreHistoryDTO>) {
    if (scores.isEmpty()) return
    val chartData = scores.take(7).reversed()

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TIEN TRINH SUC KHOE (7 TUAN GAN NHAT)",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E).copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { point ->
                    val barHeightFraction = (point.healthScore.toFloat() / 100f).coerceIn(0.1f, 1f)
                    val barColor = when {
                        point.healthScore >= 80 -> GreenSuccess
                        point.healthScore >= 50 -> AmberWarning
                        else -> RedDanger
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = "${point.healthScore}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = barColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(barHeightFraction)
                                .background(barColor, shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val label = try {
                            val parts = point.weekStartDate.split("-")
                            "${parts[2]}/${parts[1]}"
                        } catch (e: Exception) {
                            "Tuan"
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1A237E).copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreHistoryItem(item: ScoreHistoryDTO) {
    val scoreColor = when {
        item.healthScore >= 80 -> GreenSuccess
        item.healthScore >= 50 -> AmberWarning
        else -> RedDanger
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val formattedWeek = try {
                        val parts = item.weekStartDate.split("-")
                        "Tuan ${parts[2]}/${parts[1]}/${parts[0]}"
                    } catch (e: Exception) {
                        "Tuan ${item.weekStartDate}"
                    }
                    Text(
                        text = formattedWeek,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.W800,
                        color = Color(0xFF1A237E)
                    )
                    Text(
                        text = "Che do: ${item.debtMode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1A237E).copy(alpha = 0.5f),
                        fontWeight = FontWeight.W700
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Progress change indicator
                    if (item.progressScore != 0) {
                        val isPositive = item.progressScore > 0
                        val progColor = if (isPositive) GreenSuccess else RedDanger
                        val progIcon = if (isPositive) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = progIcon,
                                contentDescription = null,
                                tint = progColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${if (isPositive) "+" else ""}${item.progressScore}",
                                style = MaterialTheme.typography.labelMedium,
                                color = progColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = scoreColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${item.healthScore} d",
                            color = scoreColor,
                            fontWeight = FontWeight.W900,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFF1A237E).copy(alpha = 0.06f))

            // Score details breakdown grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreFactorBadge(label = "Chi tieu", score = item.spendingScore, max = 35)
                ScoreFactorBadge(label = "No / Quy", score = item.debtScore, max = 35)
                ScoreFactorBadge(label = "Tiet kiem", score = item.savingScore, max = 20)
                ScoreFactorBadge(label = "Canh giac", score = item.awarenessScore, max = 10)
            }

            if (item.insights.isNotBlank() && item.insights != "[]") {
                val cleanInsights = item.insights
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",")
                    .filter { it.isNotBlank() }

                if (cleanInsights.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF1A237E).copy(alpha = 0.03f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        cleanInsights.forEach { insight ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = Color(0xFF1A237E).copy(alpha = 0.4f),
                                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                )
                                Text(
                                    text = insight.trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1A237E).copy(alpha = 0.7f),
                                    fontWeight = FontWeight.W600
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreFactorBadge(label: String, score: Int, max: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF1A237E).copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
        Text(
            text = "$score/$max",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E).copy(alpha = 0.9f)
        )
    }
}

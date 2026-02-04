package com.msis.adhittan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msis.adhittan.AdhittanApplication
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.OffWhite
import com.msis.adhittan.ui.theme.SoftClay
import com.msis.adhittan.ui.theme.TransformativeTeal
import com.msis.adhittan.ui.theme.WarmSand
import com.msis.adhittan.ui.viewmodel.AppViewModelProvider
import com.msis.adhittan.ui.viewmodel.HomeViewModel

@Composable
fun DashboardScreen(
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToBeads: () -> Unit,
) {
    val activeSession by viewModel.activeSession.collectAsState()
    var showNewSessionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val libraryRepository = (context.applicationContext as AdhittanApplication).container.libraryRepository

    if (showNewSessionDialog) {
        NewAdhittanDialog(
            onDismiss = { showNewSessionDialog = false },
            onStart = { chantId, days, goal ->
                viewModel.startNewSession(chantId, days, goal)
                showNewSessionDialog = false
            },
            libraryRepository = libraryRepository
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMoss)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (activeSession == null) {
            // Empty State
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Home,
                contentDescription = null,
                tint = WarmSand,
                modifier = Modifier.size(80.dp).padding(bottom = 24.dp)
            )
            Text(
                text = "No active Adhittan.\nStart your journey today.",
                color = OffWhite.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = { showNewSessionDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = TransformativeTeal),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Start New Determination", fontWeight = FontWeight.Bold)
            }
        } else {
            // Standard Session Card
            val session = activeSession!!
            val progress = session.dailyProgress[session.currentDay] ?: 0
            val percent = (progress.toFloat() / session.targetCountPerDay).coerceAtMost(1f) * 100

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftClay)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "ACTIVE ADHITTAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepMoss.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = session.chantId.replace("sutta/", "").removeSuffix(".txt").replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepMoss,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("DAYS", style = MaterialTheme.typography.labelSmall, color = DeepMoss.copy(alpha = 0.6f))
                            Text("${session.currentDay} / ${session.durationDays}", style = MaterialTheme.typography.titleLarge, color = DeepMoss)
                        }
                        Column {
                            Text("TARGET", style = MaterialTheme.typography.labelSmall, color = DeepMoss.copy(alpha = 0.6f))
                            Text("${session.targetCountPerDay} Beads", style = MaterialTheme.typography.titleLarge, color = DeepMoss)
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                         Text("PROGRESS", style = MaterialTheme.typography.labelSmall, color = DeepMoss)
                         Text("${percent.toInt()}%", style = MaterialTheme.typography.labelSmall, color = DeepMoss, fontWeight = FontWeight.Bold)
                    }
                    LinearProgressIndicator(
                        progress = percent / 100f,
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = TransformativeTeal,
                        trackColor = DeepMoss.copy(alpha = 0.1f)
                    )

                    Button(
                        onClick = onNavigateToBeads,
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepMoss)
                    ) {
                        Text("Continue Journey", color = OffWhite)
                    }

                    TextButton(
                        onClick = viewModel::finishSession,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Clear Determination", color = DeepMoss.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

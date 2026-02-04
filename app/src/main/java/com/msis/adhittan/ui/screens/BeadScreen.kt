package com.msis.adhittan.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.OffWhite
import com.msis.adhittan.ui.theme.SoftClay
import com.msis.adhittan.ui.theme.WarmSand
import com.msis.adhittan.ui.viewmodel.AppViewModelProvider
import com.msis.adhittan.ui.viewmodel.BeadViewModel
import com.msis.adhittan.ui.viewmodel.BeadUiState
import com.msis.adhittan.ui.components.ChantReaderView

@Composable
fun BeadScreen(
    viewModel: BeadViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showText by remember { mutableStateOf(false) }

    // Haptics Helper
    fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    LaunchedEffect(state.sessionActive) {
       // if active, maybe load chant text if needed
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMoss)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!state.sessionActive) {
            Text(
                text = "No active determination",
                color = OffWhite.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
             Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Goal Reached
                if (state.isGoalReached) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = WarmSand),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = "Daily Goal Reached! 🎉",
                            color = DeepMoss,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                }

                // Chant Toggle
                Button(
                    onClick = { showText = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepMoss.copy(alpha = 0.2f),
                        contentColor = WarmSand
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.MenuBook, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Read Chant")
                }

                if (showText) {
                    BackHandler { showText = false }
                    val chantTitle = state.chantText?.let { "Chant Text" } ?: "Loading..."
                    val chantContent = state.chantText ?: "Loading text..."
                    
                     ChantReaderView(
                        title = chantTitle,
                        content = chantContent,
                        onDismiss = { showText = false },
                        extraContent = {
                             BeadCounterRing(
                                state = state,
                                size = 220.dp,
                                onClick = {
                                    viewModel.increment()
                                    state.takeIf { it.vibrationEnabled }?.let { vibrate() }
                                }
                            )
                        }
                    )
                } else {
                    // Bead Counter Ring (Standard View)
                    BeadCounterRing(
                        state = state,
                        size = 300.dp,
                        onClick = {
                            viewModel.increment()
                            if (state.vibrationEnabled) vibrate()
                        }
                    )
                    
                    TextButton(onClick = viewModel::reset) {
                        Text("Reset", color = OffWhite.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

@Composable
fun BeadCounterRing(
    state: BeadUiState,
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material.ripple.rememberRipple(color = WarmSand)
            ) { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (this.size.minDimension - strokeWidth) / 2
            
            // Track
            drawCircle(
                color = SoftClay.copy(alpha = 0.2f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )
            
            // Progress
            val progress = (state.count.toFloat() / state.target).coerceIn(0f, 1f)
            drawArc(
                color = if (state.isGoalReached) WarmSand else SoftClay,
                startAngle = -90f,
                sweepAngle = 360 * progress,
                useCenter = false,
                topLeft = Offset((this.size.width - radius * 2) / 2, (this.size.height - radius * 2) / 2),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "DAY ${state.currentDay}",
                style = MaterialTheme.typography.labelSmall,
                color = WarmSand.copy(alpha = 0.6f),
                letterSpacing = 2.sp
            )
            Text(
                text = "${state.count}",
                style = MaterialTheme.typography.displayLarge,
                color = OffWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/ ${state.target}",
                style = MaterialTheme.typography.titleMedium,
                color = WarmSand
            )
            Text(
                text = "TAP TO COUNT",
                style = MaterialTheme.typography.labelSmall,
                color = OffWhite.copy(alpha = 0.3f),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msis.adhittan.data.model.NawinDay
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.OffWhite
import com.msis.adhittan.ui.theme.SoftClay
import com.msis.adhittan.ui.theme.WarmSand
import com.msis.adhittan.ui.viewmodel.AppViewModelProvider
import com.msis.adhittan.ui.viewmodel.NawinViewModel

@Composable
fun NawinBeadScreen(
    onBack: () -> Unit,
    viewModel: NawinViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    BackHandler(onBack = onBack)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMoss)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!state.isActive || state.currentNawinDay == null) {
            Text("No active Nawin session", color = OffWhite.copy(alpha = 0.5f))
        } else {
            val nawinDay = state.currentNawinDay!!
            val planetColor = Color(android.graphics.Color.parseColor(nawinDay.colorHex))

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Info Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SoftClay.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DAY ${state.index + 1} OF 81",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmSand.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Attribute: ${nawinDay.attributeName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmSand,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Mantra count based on round ID",
                            style = MaterialTheme.typography.bodySmall,
                            color = OffWhite.copy(alpha = 0.7f)
                        )
                    }
                }

                // Nawin Progress Ring
                NawinCounterRing(
                    count = state.count,
                    target = nawinDay.mantraCount,
                    planetColor = planetColor,
                    size = 300.dp,
                    onClick = {
                        viewModel.increment()
                        if (state.vibrationEnabled) vibrate()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (state.isGoalReached) {
                    if (state.index < 80) {
                        Button(
                            onClick = viewModel::nextDay,
                            colors = ButtonDefaults.buttonColors(containerColor = WarmSand, contentColor = DeepMoss)
                        ) {
                            Text("Proceed to Day ${state.index + 2}")
                            Icon(Icons.Default.ChevronRight, null)
                        }
                    } else {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = planetColor),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                "81-Day Victory Achieved! 🎉",
                                color = DeepMoss,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                TextButton(onClick = viewModel::reset, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Reset Day", color = OffWhite.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
fun NawinCounterRing(
    count: Int,
    target: Int,
    planetColor: Color,
    size: Dp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material.ripple.rememberRipple(color = planetColor)
            ) { onClick() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val radius = (this.size.minDimension - strokeWidth) / 2
            
            // Track
            drawCircle(
                color = planetColor.copy(alpha = 0.1f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )
            
            // Progress
            val progress = (count.toFloat() / target).coerceIn(0f, 1f)
            drawArc(
                color = planetColor,
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
                text = "$count",
                style = MaterialTheme.typography.displayLarge,
                color = OffWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/ $target beads",
                style = MaterialTheme.typography.titleMedium,
                color = planetColor.copy(alpha = 0.8f)
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

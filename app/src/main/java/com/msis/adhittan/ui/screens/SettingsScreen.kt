package com.msis.adhittan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.OffWhite
import com.msis.adhittan.ui.theme.SoftClay
import com.msis.adhittan.ui.viewmodel.AppViewModelProvider
import com.msis.adhittan.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepMoss)
            .padding(16.dp)
    ) {
        Card(
             colors = CardDefaults.cardColors(containerColor = SoftClay),
             modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SETTINGS", style = MaterialTheme.typography.labelSmall, color = DeepMoss.copy(alpha = 0.6f))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Haptic Feedback", color = DeepMoss, style = MaterialTheme.typography.titleMedium)
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = { viewModel.setVibration(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepMoss,
                            checkedTrackColor = DeepMoss.copy(alpha = 0.2f)
                        )
                    )
                }
                
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Language", color = DeepMoss, style = MaterialTheme.typography.titleMedium)
                    
                    val currentLanguage by viewModel.language.collectAsState()
                    
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                         Text(
                            text = if (currentLanguage == "my") "Myanmar" else "English",
                            color = DeepMoss.copy(alpha = 0.6f),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Switch(
                            checked = currentLanguage == "my",
                            onCheckedChange = { isMyanmar ->
                                viewModel.setLanguage(if (isMyanmar) "my" else "en")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DeepMoss,
                                checkedTrackColor = DeepMoss.copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Adhittan Master v1.0.0\nSacred Tool for Mindfulness",
            color = OffWhite.copy(alpha = 0.3f),
            style = MaterialTheme.typography.labelSmall,
             modifier = Modifier.fillMaxWidth(),
             textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

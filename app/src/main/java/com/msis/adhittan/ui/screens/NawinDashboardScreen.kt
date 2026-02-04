package com.msis.adhittan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.msis.adhittan.ui.theme.WarmSand
import com.msis.adhittan.ui.viewmodel.AppViewModelProvider
import com.msis.adhittan.ui.viewmodel.HomeViewModel

@Composable
fun NawinDashboardScreen(
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateToNawinBeads: () -> Unit
) {
    val isNawinActive by homeViewModel.isNawinActive.collectAsState()
    var showNewNawinDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val libraryRepository = (context.applicationContext as AdhittanApplication).container.libraryRepository

    if (showNewNawinDialog) {
        NewNawinDialog(
            onDismiss = { showNewNawinDialog = false },
            onStart = {
                homeViewModel.startNawinSession()
                showNewNawinDialog = false
            }
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
        if (!isNawinActive) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = WarmSand,
                modifier = Modifier.size(80.dp).padding(bottom = 24.dp)
            )
            Text(
                text = "KO NAWIN\n9-Day Victory Journey",
                color = WarmSand,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Follow the traditional Myanmar planetary paths to achieve your spiritual goals.",
                color = OffWhite.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
            )
            Button(
                onClick = { showNewNawinDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = WarmSand, contentColor = DeepMoss),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Start Ko Nawin Journey", fontWeight = FontWeight.Bold)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SoftClay)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "ACTIVE KO NAWIN",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepMoss.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "9-Day Victory Path",
                        style = MaterialTheme.typography.headlineMedium,
                        color = DeepMoss,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                    
                    Text(
                        "You are currently on a planetary jump sequence. Success is near.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DeepMoss.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNavigateToNawinBeads,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepMoss)
                    ) {
                        Text("Continue Nawin", color = OffWhite)
                    }

                    TextButton(
                        onClick = homeViewModel::finishSession,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Clear Determination", color = DeepMoss.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

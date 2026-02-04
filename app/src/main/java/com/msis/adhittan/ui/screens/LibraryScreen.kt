package com.msis.adhittan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.SoftClay
import com.msis.adhittan.ui.theme.WarmSand
import com.msis.adhittan.ui.viewmodel.AppViewModelProvider
import com.msis.adhittan.ui.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Box(modifier = Modifier.fillMaxSize().background(DeepMoss)) {
        if (viewModel.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = WarmSand
            )
        } else if (viewModel.textContent != null) {
            // Content View
            Column(modifier = Modifier.padding(16.dp)) {
                TextButton(
                    onClick = viewModel::goBack,
                    colors = ButtonDefaults.textButtonColors(contentColor = WarmSand)
                ) {
                    Text("← Back", fontWeight = FontWeight.Bold)
                }
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftClay),
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())
                    ) {
                         Text(
                            text = viewModel.textContent!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = DeepMoss
                        )
                    }
                }
            }
        } else {
            // List View
            Column(modifier = Modifier.padding(16.dp)) {
                 if (viewModel.currentPath != "assets") {
                    TextButton(
                        onClick = viewModel::goBack,
                        colors = ButtonDefaults.textButtonColors(contentColor = WarmSand)
                    ) {
                        Text("← Back", fontWeight = FontWeight.Bold)
                    }
                }
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(viewModel.menuItems) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SoftClay),
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (item.filename == "..") {
                                    viewModel.loadContent("..") // Trigger submenu logic in VM
                                } else {
                                    viewModel.loadContent(item.filename)
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepMoss,
                                    modifier = Modifier.weight(1f)
                                )
                                Text("→", color = DeepMoss.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }
}

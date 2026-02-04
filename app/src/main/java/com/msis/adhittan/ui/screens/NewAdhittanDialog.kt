package com.msis.adhittan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.msis.adhittan.data.model.MenuItem
import com.msis.adhittan.data.repository.LibraryRepository
import com.msis.adhittan.ui.theme.DeepMoss
import com.msis.adhittan.ui.theme.OffWhite
import com.msis.adhittan.ui.theme.SoftClay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAdhittanDialog(
    onDismiss: () -> Unit,
    onStart: (chantId: String, days: Int, goal: Int) -> Unit,
    libraryRepository: LibraryRepository
) {
    var days by remember { mutableStateOf("7") }
    var goal by remember { mutableStateOf("108") }
    var chantId by remember { mutableStateOf("") }
    
    var availableChants by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Dropdown state
    var expanded by remember { mutableStateOf(false) }
    var selectedChantTitle by remember { mutableStateOf("Select a Chant") }

    LaunchedEffect(Unit) {
        val chants = libraryRepository.getAllChants()
        availableChants = chants
        if (chants.isNotEmpty()) {
            chantId = chants[0].filename
            selectedChantTitle = chants[0].title
        }
        isLoading = false
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftClay)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start New Determination",
                    style = MaterialTheme.typography.titleLarge,
                    color = DeepMoss,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (isLoading) {
                    CircularProgressIndicator(color = DeepMoss)
                } else {
                    // Chant Selection
                    Text("CHOOSE A CHANT", style = MaterialTheme.typography.labelSmall, color = DeepMoss.copy(alpha = 0.6f))
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedChantTitle,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepMoss,
                                    unfocusedBorderColor = DeepMoss.copy(alpha = 0.5f),
                                    focusedTextColor = DeepMoss,
                                    unfocusedTextColor = DeepMoss
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(SoftClay)
                            ) {
                                availableChants.forEach { chant ->
                                    DropdownMenuItem(
                                        text = { Text(chant.title, color = DeepMoss) },
                                        onClick = {
                                            selectedChantTitle = chant.title
                                            chantId = chant.filename
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Days
                    Text("DURATION (DAYS)", style = MaterialTheme.typography.labelSmall, color = DeepMoss.copy(alpha = 0.6f))
                    OutlinedTextField(
                        value = days,
                        onValueChange = { days = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepMoss,
                            unfocusedBorderColor = DeepMoss.copy(alpha = 0.5f),
                            focusedTextColor = DeepMoss,
                            unfocusedTextColor = DeepMoss
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    // Goal
                    Text("DAILY GOAL (BEADS)", style = MaterialTheme.typography.labelSmall, color = DeepMoss.copy(alpha = 0.6f))
                    OutlinedTextField(
                        value = goal,
                        onValueChange = { goal = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepMoss,
                            unfocusedBorderColor = DeepMoss.copy(alpha = 0.5f),
                            focusedTextColor = DeepMoss,
                            unfocusedTextColor = DeepMoss
                        ),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    )

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CANCEL", color = DeepMoss.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                onStart(chantId, days.toIntOrNull() ?: 7, goal.toIntOrNull() ?: 108)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepMoss)
                        ) {
                            Text("START", color = OffWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

package com.lincelx.sysadmtools.ui.screens.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lincelx.sysadmtools.R
import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.data.model.Visit
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitDialog(
    clients: List<Client>,
    onDismiss: () -> Unit,
    onConfirm: (clientId: String, time: LocalTime?) -> Unit,
    onDelete: (() -> Unit)? = null,
    initialVisit: Visit? = null,
) {
    var selectedClientId by remember { mutableStateOf(initialVisit?.clientId ?: "") }
    var selectedTime by remember { mutableStateOf(initialVisit?.time) }
    var showTimePicker by remember { mutableStateOf(false) }
    var expandedClients by remember { mutableStateOf(false) }

    val selectedClient = clients.find { it.id == selectedClientId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialVisit == null) {
                    stringResource(R.string.add_visit)
                } else {
                    "Редактировать визит"
                }
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Client selection
                if (initialVisit == null) {
                    Box {
                        OutlinedTextField(
                            value = selectedClient?.name ?: "Выберите клиента",
                            onValueChange = {},
                            label = { Text("Клиент") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedClients = !expandedClients }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expandedClients = true }
                        )
                        DropdownMenu(
                            expanded = expandedClients,
                            onDismissRequest = { expandedClients = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            clients.forEach { client ->
                                DropdownMenuItem(
                                    text = { Text(client.name) },
                                    onClick = {
                                        selectedClientId = client.id
                                        expandedClients = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = selectedClient?.name ?: "Неизвестный клиент",
                        onValueChange = {},
                        label = { Text("Клиент") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Time selection
                Column {
                    Text(
                        text = "Время (опционально)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                    ?: "Выбрать время"
                            )
                        }
                        if (selectedTime != null) {
                            IconButton(onClick = { selectedTime = null }) {
                                Icon(Icons.Default.Clear, contentDescription = "Очистить время")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить")
                    }
                }
                
                Button(
                    onClick = {
                        if (selectedClientId.isNotBlank()) {
                            onConfirm(selectedClientId, selectedTime)
                        }
                    },
                    enabled = selectedClientId.isNotBlank()
                ) {
                    Text(if (initialVisit == null) "Добавить" else "Сохранить")
                }
            }
        },
        dismissButton = null
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime?.hour ?: LocalTime.now().hour,
            initialMinute = selectedTime?.minute ?: LocalTime.now().minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

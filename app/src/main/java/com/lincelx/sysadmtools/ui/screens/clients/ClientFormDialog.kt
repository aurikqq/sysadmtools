package com.lincelx.sysadmtools.ui.screens.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.data.model.CustomField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientFormDialog(
    onDismiss: () -> Unit,
    onSave: (Client) -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var customFields by remember { mutableStateOf<List<CustomField>>(emptyList()) }
    var nameError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Новый клиент") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Назад",
                                )
                            }
                        },
                        actions = {
                            TextButton(
                                onClick = {
                                    if (name.isBlank()) {
                                        nameError = true
                                    } else {
                                        onSave(
                                            Client(
                                                name = name.trim(),
                                                address = address.trim(),
                                                phone = phone.trim(),
                                                note = note.trim(),
                                                customFields = customFields
                                                    .filter { it.name.isNotBlank() || it.value.isNotBlank() }
                                                    .map {
                                                        it.copy(
                                                            name = it.name.trim(),
                                                            value = it.value.trim(),
                                                        )
                                                    },
                                            ),
                                        )
                                    }
                                },
                            ) {
                                Text("Сохранить")
                            }
                        },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        label = { Text("Имя / название *") },
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text("Обязательное поле") }
                        } else {
                            null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Адрес") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Телефон") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Заметка") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Дополнительные поля",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        IconButton(
                            onClick = {
                                customFields = customFields + CustomField()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Добавить поле",
                            )
                        }
                    }

                    customFields.forEach { field ->
                        CustomFieldEditor(
                            field = field,
                            onNameChange = { newName ->
                                customFields = customFields.map {
                                    if (it.id == field.id) it.copy(name = newName) else it
                                }
                            },
                            onValueChange = { newValue ->
                                customFields = customFields.map {
                                    if (it.id == field.id) it.copy(value = newValue) else it
                                }
                            },
                            onDelete = {
                                customFields = customFields.filter { it.id != field.id }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomFieldEditor(
    field: CustomField,
    onNameChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        OutlinedTextField(
            value = field.name,
            onValueChange = onNameChange,
            label = { Text("Название поля") },
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
        OutlinedTextField(
            value = field.value,
            onValueChange = onValueChange,
            label = { Text("Значение") },
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Удалить поле",
            )
        }
    }
}

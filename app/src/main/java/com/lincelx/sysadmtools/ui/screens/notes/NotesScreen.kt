package com.lincelx.sysadmtools.ui.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog as M3Dialog
import androidx.compose.ui.window.DialogProperties
import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.data.model.Note
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier,
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadClients()
        viewModel.loadNotes()
    }
    val filteredNotes = viewModel.filteredNotes

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = {
                    viewModel.searchQuery = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск",
                    )
                },
                placeholder = {
                    Text("Поиск по заметкам")
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
            )

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Заметки не найдены",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 88.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = filteredNotes,
                        key = { it.id },
                    ) { note ->
                        val noteClients = viewModel.allClients.filter { 
                            it.id in note.clientIds
                        }
                        NoteCard(
                            note = note,
                            clients = noteClients,
                            onDelete = {
                                viewModel.deleteNote(note.id)
                            },
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                viewModel.openAddDialog()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить заметку",
            )
        }

        if (viewModel.showAddDialog) {
            AddNoteDialog(
                onDismiss = viewModel::closeAddDialog,
                onSave = viewModel::addNote,
                clients = viewModel.allClients,
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    clients: List<Client>,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить заметку",
                    )
                }
            }
            if (clients.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val clientNames = clients.joinToString(", ") { it.name }
                Text(
                    text = "Клиенты: $clientNames",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.createdAt.format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit,
    clients: List<Client>,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedClientIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var titleError by remember { mutableStateOf(false) }

    M3Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Новая заметка",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть",
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        label = { Text("Название *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = titleError,
                    )
                }

                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Текст заметки") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5,
                    )
                }

                item {
                    Text(
                        text = "Связанные клиенты",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                if (clients.isEmpty()) {
                    item {
                        Text(
                            text = "Нет сохраненных клиентов",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    items(
                        items = clients,
                        key = { it.id }
                    ) { client ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = client.name)
                            Checkbox(
                                checked = selectedClientIds.contains(client.id),
                                onCheckedChange = { checked ->
                                    selectedClientIds = if (checked) {
                                        selectedClientIds + client.id
                                    } else {
                                        selectedClientIds - client.id
                                    }
                                },
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                            } else {
                                onSave(
                                    Note.create(
                                        id = java.util.UUID.randomUUID().toString(),
                                        title = title.trim(),
                                        content = content.trim(),
                                        clientIds = selectedClientIds.toList(),
                                        createdAt = java.time.LocalDateTime.now(),
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}

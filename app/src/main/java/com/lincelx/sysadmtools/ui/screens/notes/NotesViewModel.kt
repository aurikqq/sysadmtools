package com.lincelx.sysadmtools.ui.screens.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import com.lincelx.sysadmtools.data.repository.ClientRepository
import com.lincelx.sysadmtools.data.repository.NotesRepository
import kotlinx.coroutines.launch

class NotesViewModel : ViewModel() {
    var notes by mutableStateOf<List<com.lincelx.sysadmtools.data.model.Note>>(emptyList())
        private set

    var allClients by mutableStateOf<List<com.lincelx.sysadmtools.data.model.Client>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")

    var selectedClientId by mutableStateOf<String?>(null)
        private set

    var showAddDialog by mutableStateOf(false)
        private set

    private lateinit var notesRepository: NotesRepository
    private lateinit var clientRepository: ClientRepository

    fun init(prefs: SharedPreferencesManager) {
        notesRepository = NotesRepository(prefs)
        clientRepository = ClientRepository(prefs)
        loadNotes()
        loadClients()
    }

    fun loadNotes() {
        viewModelScope.launch {
            notes = notesRepository.getNotes()
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            allClients = clientRepository.getClients()
        }
    }

    val filteredNotes: List<com.lincelx.sysadmtools.data.model.Note>
        get() {
            return notes.filter { note ->
                val matchesSearch = note.title.contains(searchQuery, ignoreCase = true) ||
                    note.content.contains(searchQuery, ignoreCase = true) ||
                    allClients.any { client -> 
                        note.clientIds.contains(client.id) && client.name.contains(searchQuery, ignoreCase = true)
                    }
                val matchesClient = selectedClientId == null || note.clientIds.contains(selectedClientId)
                matchesSearch && matchesClient
            }
        }

    fun setSelectedClient(clientId: String?) {
        selectedClientId = clientId
    }

    fun openAddDialog() {
        loadClients()
        showAddDialog = true
    }

    fun closeAddDialog() {
        showAddDialog = false
    }

    fun addNote(note: com.lincelx.sysadmtools.data.model.Note) {
        viewModelScope.launch {
            notesRepository.addNote(note)
            notes = notesRepository.getNotes()
        }
        closeAddDialog()
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            notesRepository.deleteNote(id)
            notes = notesRepository.getNotes()
        }
    }
}

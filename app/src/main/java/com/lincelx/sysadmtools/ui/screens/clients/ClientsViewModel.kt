package com.lincelx.sysadmtools.ui.screens.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import com.lincelx.sysadmtools.data.repository.ClientRepository
import kotlinx.coroutines.launch

class ClientsViewModel : ViewModel() {
    var clients by mutableStateOf<List<Client>>(emptyList())
        private set

    var showAddForm by mutableStateOf(false)
        private set

    var selectedClientId by mutableStateOf<String?>(null)
        private set

    private lateinit var repository: ClientRepository

    fun init(prefs: SharedPreferencesManager) {
        repository = ClientRepository(prefs)
        loadClients()
    }

    fun loadClients() {
        viewModelScope.launch {
            clients = repository.getClients()
        }
    }

    fun openAddForm() {
        showAddForm = true
    }

    fun closeAddForm() {
        showAddForm = false
    }

    fun selectClient(id: String) {
        selectedClientId = id
    }

    fun closeDetail() {
        selectedClientId = null
    }

    fun addClient(client: Client) {
        viewModelScope.launch {
            repository.addClient(client)
            clients = repository.getClients()
        }
        closeAddForm()
    }

    fun deleteClient(id: String) {
        viewModelScope.launch {
            repository.deleteClient(id)
            clients = repository.getClients()
        }
        closeDetail()
    }

    fun getClient(id: String): Client? = clients.find { it.id == id }
}

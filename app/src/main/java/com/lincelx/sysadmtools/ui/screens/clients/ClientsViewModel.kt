package com.lincelx.sysadmtools.ui.screens.clients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lincelx.sysadmtools.data.model.Client

class ClientsViewModel : ViewModel() {
    var clients by mutableStateOf<List<Client>>(emptyList())
        private set

    var showAddForm by mutableStateOf(false)
        private set

    var selectedClientId by mutableStateOf<String?>(null)
        private set

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
        clients = clients + client
        closeAddForm()
    }

    fun getClient(id: String): Client? = clients.find { it.id == id }
}

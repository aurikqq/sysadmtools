package com.lincelx.sysadmtools.data.repository

import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager

class ClientRepository(private val prefs: SharedPreferencesManager) {

    fun getClients(): List<Client> = prefs.clients

    fun getClientById(id: String): Client? = prefs.clients.find { it.id == id }

    fun addClient(client: Client) {
        prefs.addClient(client)
    }

    fun updateClient(client: Client) {
        prefs.updateClient(client)
    }

    fun deleteClient(id: String) {
        prefs.deleteClient(id)
    }

    fun getCategories(): List<String> = prefs.categories

    fun addCategory(category: String) {
        prefs.addCategory(category)
    }

    fun deleteCategory(category: String) {
        prefs.deleteCategory(category)
    }
}

package com.lincelx.sysadmtools.data.prefs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lincelx.sysadmtools.data.model.AppSettings
import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.data.model.Note
import com.lincelx.sysadmtools.data.model.Visit
import java.time.LocalDate

private const val PREFS_NAME = "sysadmtools_prefs"
private const val KEY_CLIENTS = "clients"
private const val KEY_VISITS = "visits"
private const val KEY_CATEGORIES = "categories"
private const val KEY_NOTES = "notes"
private const val KEY_SETTINGS = "settings"

class SharedPreferencesManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private val clientsType = object : TypeToken<List<Client>>() {}.type
    private val visitsType = object : TypeToken<List<Visit>>() {}.type
    private val categoriesType = object : TypeToken<List<String>>() {}.type
    private val notesType = object : TypeToken<List<Note>>() {}.type
    private val settingsType = object : TypeToken<AppSettings>() {}.type

    var clients: List<Client>
        get() = prefs.getString(KEY_CLIENTS, null)?.let { gson.fromJson(it, clientsType) } ?: emptyList()
        set(value) = prefs.edit().putString(KEY_CLIENTS, gson.toJson(value)).apply()

    var visits: List<Visit>
        get() = prefs.getString(KEY_VISITS, null)?.let { gson.fromJson(it, visitsType) } ?: emptyList()
        set(value) = prefs.edit().putString(KEY_VISITS, gson.toJson(value)).apply()

    var categories: List<String>
        get() = prefs.getString(KEY_CATEGORIES, null)?.let { gson.fromJson(it, categoriesType) } ?: emptyList()
        set(value) = prefs.edit().putString(KEY_CATEGORIES, gson.toJson(value)).apply()

    var notes: List<Note>
        get() = prefs.getString(KEY_NOTES, null)?.let { gson.fromJson(it, notesType) } ?: emptyList()
        set(value) = prefs.edit().putString(KEY_NOTES, gson.toJson(value)).apply()

    fun addClient(client: Client) {
        clients = clients + client
    }

    fun updateClient(updatedClient: Client) {
        clients = clients.map { if (it.id == updatedClient.id) updatedClient else it }
    }

    fun deleteClient(id: String) {
        clients = clients.filterNot { it.id == id }
    }

    fun addVisit(visit: Visit) {
        visits = visits + visit
    }

    fun updateVisit(updatedVisit: Visit) {
        visits = visits.map { if (it.id == updatedVisit.id) updatedVisit else it }
    }

    fun deleteVisit(id: String) {
        visits = visits.filterNot { it.id == id }
    }

    fun getVisitsForDate(date: LocalDate): List<Visit> {
        return visits.filter { it.date == date }
    }

    fun getVisitsForClient(clientId: String): List<Visit> {
        return visits.filter { it.clientId == clientId }
    }

    fun addCategory(category: String) {
        if (category.isNotBlank() && !categories.contains(category)) {
            categories = categories + category
        }
    }

    fun deleteCategory(category: String) {
        categories = categories.filterNot { it == category }
    }

    fun addNote(note: Note) {
        notes = notes + note
    }

    fun updateNote(updatedNote: Note) {
        notes = notes.map { if (it.id == updatedNote.id) updatedNote else it }
    }

    fun deleteNote(id: String) {
        notes = notes.filterNot { it.id == id }
    }

    fun getNotesForClient(clientId: String): List<Note> {
        return notes.filter { note -> clientId in note.clientIds }
    }

    var settings: AppSettings
        get() = prefs.getString(KEY_SETTINGS, null)?.let { gson.fromJson(it, settingsType) } ?: AppSettings()
        set(value) = prefs.edit().putString(KEY_SETTINGS, gson.toJson(value)).apply()

    fun updateSettings(updates: AppSettings.() -> AppSettings) {
        settings = settings.updates()
    }
}

package com.lincelx.sysadmtools.data.repository

import com.lincelx.sysadmtools.data.model.Note
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager

class NotesRepository(private val prefs: SharedPreferencesManager) {

    fun getNotes(): List<Note> = prefs.notes

    fun getNoteById(id: String): Note? = prefs.notes.find { it.id == id }

    fun addNote(note: Note) {
        prefs.addNote(note)
    }

    fun updateNote(note: Note) {
        prefs.updateNote(note)
    }

    fun deleteNote(id: String) {
        prefs.deleteNote(id)
    }

    fun getNotesForClient(clientId: String): List<Note> = prefs.getNotesForClient(clientId)
}

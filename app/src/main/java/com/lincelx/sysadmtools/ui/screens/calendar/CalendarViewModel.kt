package com.lincelx.sysadmtools.ui.screens.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import com.lincelx.sysadmtools.data.repository.ClientRepository
import com.lincelx.sysadmtools.data.repository.VisitRepository
import java.time.LocalDate
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {
    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var isCalendarCollapsed by mutableStateOf(true)
        private set

    var visits by mutableStateOf<List<com.lincelx.sysadmtools.data.model.Visit>>(emptyList())
        private set

    var clients by mutableStateOf<List<com.lincelx.sysadmtools.data.model.Client>>(emptyList())
        private set

    var showClientPicker by mutableStateOf(false)
        private set

    var selectedVisitForEdit by mutableStateOf<com.lincelx.sysadmtools.data.model.Visit?>(null)
        private set

    private lateinit var visitRepository: VisitRepository
    private lateinit var clientRepository: ClientRepository
    private var sharedPrefs: SharedPreferencesManager? = null
    private var applicationContext: android.content.Context? = null

    fun init(context: android.content.Context, prefs: SharedPreferencesManager) {
        applicationContext = context.applicationContext
        sharedPrefs = prefs
        visitRepository = VisitRepository(prefs)
        clientRepository = ClientRepository(prefs)
        loadVisits()
        loadClients()
    }

    fun loadVisits() {
        viewModelScope.launch {
            visits = visitRepository.getVisits()
        }
    }

    fun loadClients() {
        viewModelScope.launch {
            clients = clientRepository.getClients()
        }
    }

    fun selectDate(date: LocalDate) {
        selectedDate = date
        isCalendarCollapsed = true
    }

    fun getVisitCount(date: LocalDate): Int =
        visits.count { it.date == date }

    fun getVisitsForDate(date: LocalDate): List<com.lincelx.sysadmtools.data.model.Visit> =
        visits.filter { it.date == date }
            .sortedWith(
                compareBy<com.lincelx.sysadmtools.data.model.Visit> { it.time == null }
                    .thenBy { it.time }
            )

    private fun refreshNotification() {
        applicationContext?.let { context ->
            sharedPrefs?.let { prefs ->
                com.lincelx.sysadmtools.util.SettingsSideEffects.refreshPersistentNotification(
                    context,
                    prefs.settings
                )
            }
        }
    }

    fun addVisit(date: LocalDate, clientId: String, time: java.time.LocalTime? = null) {
        val newVisit = com.lincelx.sysadmtools.data.model.Visit(date = date, clientId = clientId, time = time)
        viewModelScope.launch {
            visitRepository.addVisit(newVisit)
            visits = visitRepository.getVisits()
            refreshNotification()
        }
        showClientPicker = false
    }

    fun updateVisit(visit: com.lincelx.sysadmtools.data.model.Visit) {
        viewModelScope.launch {
            visitRepository.updateVisit(visit)
            visits = visitRepository.getVisits()
            refreshNotification()
        }
        selectedVisitForEdit = null
    }

    fun removeVisit(visitId: String) {
        viewModelScope.launch {
            visitRepository.deleteVisit(visitId)
            visits = visitRepository.getVisits()
            refreshNotification()
        }
        selectedVisitForEdit = null
    }

    fun openEditDialog(visit: com.lincelx.sysadmtools.data.model.Visit) {
        selectedVisitForEdit = visit
    }

    fun closeEditDialog() {
        selectedVisitForEdit = null
    }

    fun openClientPicker() {
        showClientPicker = true
    }

    fun closeClientPicker() {
        showClientPicker = false
    }

    fun isPastDate(date: LocalDate): Boolean = date.isBefore(LocalDate.now())
}

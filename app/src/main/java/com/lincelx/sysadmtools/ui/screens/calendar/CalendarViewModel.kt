package com.lincelx.sysadmtools.ui.screens.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.lincelx.sysadmtools.data.model.Visit
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    var selectedDate by mutableStateOf<LocalDate?>(LocalDate.now())
        private set

    var isCalendarCollapsed by mutableStateOf(true)
        private set

    var visits by mutableStateOf<List<Visit>>(emptyList())
        private set

    var showClientPicker by mutableStateOf(false)
        private set

    fun selectDate(date: LocalDate) {
        selectedDate = date
        isCalendarCollapsed = true
    }

    fun getVisitCount(date: LocalDate): Int =
        visits.count { it.date == date }

    fun getVisitsForDate(date: LocalDate): List<Visit> =
        visits.filter { it.date == date }

    fun addVisit(date: LocalDate, clientId: String) {
        if (visits.none { it.date == date && it.clientId == clientId }) {
            visits = visits + Visit(date = date, clientId = clientId)
        }
        showClientPicker = false
    }

    fun removeVisit(visitId: String) {
        visits = visits.filter { it.id != visitId }
    }

    fun openClientPicker() {
        showClientPicker = true
    }

    fun closeClientPicker() {
        showClientPicker = false
    }

    fun isPastDate(date: LocalDate): Boolean = date.isBefore(LocalDate.now())
}

package com.lincelx.sysadmtools.data.repository

import com.lincelx.sysadmtools.data.model.Visit
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import java.time.LocalDate

class VisitRepository(private val prefs: SharedPreferencesManager) {

    fun getVisits(): List<Visit> = prefs.visits

    fun getVisitsForDate(date: LocalDate): List<Visit> = prefs.getVisitsForDate(date)

    fun getVisitById(id: String): Visit? = prefs.visits.find { it.id == id }

    fun addVisit(visit: Visit) {
        prefs.addVisit(visit)
    }

    fun updateVisit(visit: Visit) {
        prefs.updateVisit(visit)
    }

    fun deleteVisit(id: String) {
        prefs.deleteVisit(id)
    }
}

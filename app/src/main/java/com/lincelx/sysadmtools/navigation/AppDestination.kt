package com.lincelx.sysadmtools.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
) {
    Calendar(
        route = "calendar",
        titleRes = com.lincelx.sysadmtools.R.string.nav_calendar,
        icon = Icons.Default.CalendarMonth,
    ),
    Clients(
        route = "clients",
        titleRes = com.lincelx.sysadmtools.R.string.nav_clients,
        icon = Icons.Default.People,
    ),
    Notes(
        route = "notes",
        titleRes = com.lincelx.sysadmtools.R.string.nav_notes,
        icon = Icons.AutoMirrored.Filled.Notes,
    ),
}

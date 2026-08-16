package com.lincelx.sysadmtools.ui.screens.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lincelx.sysadmtools.ui.screens.clients.ClientsViewModel
import java.time.YearMonth

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    clientsViewModel: ClientsViewModel,
    modifier: Modifier = Modifier,
) {
    var currentMonth by remember { mutableStateOf(YearMonth.from(calendarViewModel.selectedDate)) }

    Column(modifier = modifier.fillMaxSize()) {
        MonthCalendar(
            yearMonth = currentMonth,
            selectedDate = calendarViewModel.selectedDate,
            collapsed = calendarViewModel.isCalendarCollapsed,
            visitCountForDate = calendarViewModel::getVisitCount,
            isPastDate = calendarViewModel::isPastDate,
            onDateSelected = { date ->
                calendarViewModel.selectDate(date)
                currentMonth = YearMonth.from(date)
            },
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        DayVisitsList(
            visits = calendarViewModel.getVisitsForDate(calendarViewModel.selectedDate),
            clients = clientsViewModel.clients,
            onVisitClick = calendarViewModel::openEditDialog,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}

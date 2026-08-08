package com.lincelx.sysadmtools.ui.screens.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lincelx.sysadmtools.data.model.Client
import com.lincelx.sysadmtools.ui.screens.clients.ClientsViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    clientsViewModel: ClientsViewModel,
    modifier: Modifier = Modifier,
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val selectedDate = calendarViewModel.selectedDate

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("ru"))
    }

    Column(modifier = modifier.fillMaxSize()) {
        MonthCalendar(
            yearMonth = currentMonth,
            selectedDate = selectedDate,
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

        selectedDate?.let { date ->
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = date.format(dateFormatter),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            DayVisitsList(
                visits = calendarViewModel.getVisitsForDate(date),
                clients = clientsViewModel.clients,
                onRemoveVisit = calendarViewModel::removeVisit,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

package com.lincelx.sysadmtools.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lincelx.sysadmtools.data.model.AppSettings
import com.lincelx.sysadmtools.data.model.AppTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Тема приложения",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        ThemeSelector(
            currentTheme = settings.theme,
            onThemeChange = { onSettingsChange(settings.copy(theme = it)) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Уведомления",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        NotificationSettings(
            settings = settings,
            onSettingsChange = onSettingsChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Время утреннего напоминания",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        TimePicker(
            label = "Время утреннего уведомления",
            time = settings.morningReminderTime,
            enabled = settings.morningReminderEnabled,
            onTimeChange = {
                onSettingsChange(
                    settings.copy(morningReminderTime = it)
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Рабочее время",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        WorkHoursSettings(
            settings = settings,
            onSettingsChange = onSettingsChange,
        )
    }
}

@Composable
private fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppTheme.entries.forEach { theme ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = theme.displayName)

                    RadioButton(
                        selected = currentTheme == theme,
                        onClick = { onThemeChange(theme) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettings(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Утреннее напоминание")

                Switch(
                    checked = settings.morningReminderEnabled,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(morningReminderEnabled = it)
                        )
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Постоянное уведомление")

                Switch(
                    checked = settings.persistentNotificationEnabled,
                    onCheckedChange = {
                        onSettingsChange(
                            settings.copy(persistentNotificationEnabled = it)
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun TimePicker(
    label: String,
    time: String,
    enabled: Boolean,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = label)

            if (enabled) {
                TimeInput(
                    label = "HH:mm",
                    time = time,
                    onTimeChange = onTimeChange,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Отключено",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun WorkHoursSettings(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Будние дни (пн-пт)",
                style = MaterialTheme.typography.titleMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TimeInput(
                    label = "Начало",
                    time = settings.workHoursStartWeekday,
                    onTimeChange = {
                        onSettingsChange(
                            settings.copy(workHoursStartWeekday = it)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                TimeInput(
                    label = "Конец",
                    time = settings.workHoursEndWeekday,
                    onTimeChange = {
                        onSettingsChange(
                            settings.copy(workHoursEndWeekday = it)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = "Выходные (сб-вс)",
                style = MaterialTheme.typography.titleMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TimeInput(
                    label = "Начало",
                    time = settings.workHoursStartWeekend,
                    onTimeChange = {
                        onSettingsChange(
                            settings.copy(workHoursStartWeekend = it)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )

                TimeInput(
                    label = "Конец",
                    time = settings.workHoursEndWeekend,
                    onTimeChange = {
                        onSettingsChange(
                            settings.copy(workHoursEndWeekend = it)
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeInput(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    
    val initialTime = remember(time) {
        runCatching { LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")) }
            .getOrDefault(LocalTime.of(9, 0))
    }

    androidx.compose.foundation.layout.Box(modifier = modifier) {
        TextField(
            value = time,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
        )
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showTimePicker = true }
        )
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true
        )
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    onTimeChange(newTime.format(DateTimeFormatter.ofPattern("HH:mm")))
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

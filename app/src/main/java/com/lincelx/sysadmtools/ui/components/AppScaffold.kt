package com.lincelx.sysadmtools.ui.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lincelx.sysadmtools.R
import com.lincelx.sysadmtools.data.model.AppTheme
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import com.lincelx.sysadmtools.data.repository.SettingsRepository
import com.lincelx.sysadmtools.navigation.AppDestination
import com.lincelx.sysadmtools.ui.screens.calendar.CalendarScreen
import com.lincelx.sysadmtools.ui.screens.calendar.CalendarViewModel
import com.lincelx.sysadmtools.ui.screens.calendar.VisitDialog
import com.lincelx.sysadmtools.ui.screens.clients.ClientsScreen
import com.lincelx.sysadmtools.ui.screens.clients.ClientsViewModel
import com.lincelx.sysadmtools.ui.screens.notes.NotesScreen
import com.lincelx.sysadmtools.ui.screens.notes.NotesViewModel
import com.lincelx.sysadmtools.ui.screens.settings.SettingsScreen
import com.lincelx.sysadmtools.ui.screens.settings.SettingsViewModel
import com.lincelx.sysadmtools.ui.theme.SysadmtoolsTheme
import com.lincelx.sysadmtools.util.formatDateGenitive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = AppDestination.entries.find { it.route == currentRoute }
        ?: AppDestination.Calendar

    val activity = LocalContext.current as ComponentActivity
    val prefs = SharedPreferencesManager(activity)
    
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // After notification permission request, check if we also need exact alarm permission
        if (com.lincelx.sysadmtools.util.AlarmPermissionHelper.needsExactAlarmPermission(activity)) {
            com.lincelx.sysadmtools.util.AlarmPermissionHelper.openExactAlarmSettings(activity)
        }
    }

    val clientsViewModel: ClientsViewModel = viewModel(viewModelStoreOwner = activity)
    val calendarViewModel: CalendarViewModel = viewModel(viewModelStoreOwner = activity)
    val notesViewModel: NotesViewModel = viewModel(viewModelStoreOwner = activity)
    val settingsViewModel: SettingsViewModel = viewModel(
        viewModelStoreOwner = activity,
        factory = SettingsViewModel.Factory(SettingsRepository(prefs), activity),
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        clientsViewModel.init(prefs)
        calendarViewModel.init(activity, prefs)
        notesViewModel.init(prefs)
    }

    val settings = settingsViewModel.settings
    val darkTheme = when (settings.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    val selectedDate = calendarViewModel.selectedDate
    val canAddVisit = currentDestination == AppDestination.Calendar &&
        !calendarViewModel.isPastDate(selectedDate)

    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                settingsViewModel.reapplySideEffects()
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }

    SysadmtoolsTheme(darkTheme = darkTheme) {
        if (settingsViewModel.showPermissionDialog) {
            PermissionsDialog(
                onConfirm = {
                    settingsViewModel.confirmPermissionRequest()
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else if (com.lincelx.sysadmtools.util.AlarmPermissionHelper.needsExactAlarmPermission(activity)) {
                        com.lincelx.sysadmtools.util.AlarmPermissionHelper.openExactAlarmSettings(activity)
                    }
                },
                onDismiss = settingsViewModel::dismissPermissionDialog,
            )
        }
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        when (currentDestination) {
                            AppDestination.Calendar -> {
                                Text(text = formatDateGenitive(selectedDate))
                            }
                            AppDestination.Notes -> {
                                Text(text = stringResource(currentDestination.titleRes))
                            }
                            else -> {
                                Text(text = stringResource(currentDestination.titleRes))
                            }
                        }
                    },
                    actions = {
                        // Actions removed, moved to FAB
                    },
                )
            },
            bottomBar = {
                NavigationBar {
                    AppDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = stringResource(destination.titleRes),
                                )
                            },
                            label = { Text(text = stringResource(destination.titleRes)) },
                        )
                    }
                }
            },
            floatingActionButton = {
                if (canAddVisit) {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = calendarViewModel::openClientPicker,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_visit),
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Calendar.route,
                modifier = Modifier.padding(innerPadding),
            ) {
                composable(AppDestination.Calendar.route) {
                    CalendarScreen(
                        calendarViewModel = calendarViewModel,
                        clientsViewModel = clientsViewModel,
                    )
                }
                composable(AppDestination.Clients.route) {
                    ClientsScreen(viewModel = clientsViewModel)
                }
                composable(AppDestination.Notes.route) {
                    NotesScreen(viewModel = notesViewModel)
                }
                composable(AppDestination.Settings.route) {
                    SettingsScreen(
                        settings = settingsViewModel.settings,
                        onSettingsChange = settingsViewModel::updateSettings,
                    )
                }
            }

            if (calendarViewModel.showClientPicker) {
                VisitDialog(
                    clients = clientsViewModel.clients,
                    onDismiss = calendarViewModel::closeClientPicker,
                    onConfirm = { clientId, time ->
                        calendarViewModel.addVisit(selectedDate, clientId, time)
                    }
                )
            }

            calendarViewModel.selectedVisitForEdit?.let { visit ->
                VisitDialog(
                    clients = clientsViewModel.clients,
                    onDismiss = calendarViewModel::closeEditDialog,
                    onConfirm = { _, time ->
                        calendarViewModel.updateVisit(visit.copy(time = time))
                    },
                    onDelete = {
                        calendarViewModel.removeVisit(visit.id)
                    },
                    initialVisit = visit
                )
            }
        }
    }
}

// CalendarClientDropdown removed

package com.lincelx.sysadmtools.ui.components

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lincelx.sysadmtools.navigation.AppDestination
import com.lincelx.sysadmtools.ui.screens.calendar.CalendarScreen
import com.lincelx.sysadmtools.ui.screens.calendar.CalendarViewModel
import com.lincelx.sysadmtools.ui.screens.clients.ClientsScreen
import com.lincelx.sysadmtools.ui.screens.clients.ClientsViewModel
import com.lincelx.sysadmtools.ui.screens.notes.NotesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentDestination = AppDestination.entries.find { it.route == currentRoute }
        ?: AppDestination.Calendar

    val activity = LocalContext.current as ComponentActivity
    val clientsViewModel: ClientsViewModel = viewModel(viewModelStoreOwner = activity)
    val calendarViewModel: CalendarViewModel = viewModel(viewModelStoreOwner = activity)

    val selectedDate = calendarViewModel.selectedDate
    val canAddVisit = currentDestination == AppDestination.Calendar &&
        selectedDate != null &&
        !calendarViewModel.isPastDate(selectedDate)

    val availableClients = if (selectedDate != null) {
        val scheduledIds = calendarViewModel.getVisitsForDate(selectedDate)
            .map { it.clientId }
            .toSet()
        clientsViewModel.clients.filter { it.id !in scheduledIds }
    } else {
        emptyList()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(currentDestination.titleRes)) },
                actions = {
                    when (currentDestination) {
                        AppDestination.Clients -> {
                            IconButton(onClick = clientsViewModel::openAddForm) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(
                                        com.lincelx.sysadmtools.R.string.add_client,
                                    ),
                                )
                            }
                        }
                        AppDestination.Calendar -> {
                            if (canAddVisit) {
                                Box {
                                    IconButton(onClick = calendarViewModel::openClientPicker) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = stringResource(
                                                com.lincelx.sysadmtools.R.string.add_visit,
                                            ),
                                        )
                                    }
                                    CalendarClientDropdown(
                                        expanded = calendarViewModel.showClientPicker,
                                        clients = availableClients,
                                        onDismiss = calendarViewModel::closeClientPicker,
                                        onClientSelected = { clientId ->
                                            calendarViewModel.addVisit(selectedDate!!, clientId)
                                        },
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
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
                NotesScreen()
            }
        }
    }
}

@Composable
private fun CalendarClientDropdown(
    expanded: Boolean,
    clients: List<com.lincelx.sysadmtools.data.model.Client>,
    onDismiss: () -> Unit,
    onClientSelected: (String) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        if (clients.isEmpty()) {
            DropdownMenuItem(
                text = { Text("Нет доступных компаний") },
                onClick = onDismiss,
                enabled = false,
            )
        } else {
            clients.forEach { client ->
                DropdownMenuItem(
                    text = { Text(client.name) },
                    onClick = { onClientSelected(client.id) },
                )
            }
        }
    }
}

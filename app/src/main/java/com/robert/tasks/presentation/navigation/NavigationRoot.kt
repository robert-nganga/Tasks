package com.robert.tasks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.robert.tasks.presentation.screens.AddEditTaskScreen
import com.robert.tasks.presentation.screens.TaskListScreen
import com.robert.tasks.presentation.viewmodels.AddEditTaskViewModel

@Composable
fun NavigationRoot(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(Route.Tasks)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider = { key ->
            when (key) {
                is Route.Tasks -> {
                    NavEntry(key) {
                        TaskListScreen(
                            onTaskClick = { taskId ->
                                backStack.add(Route.AddEditTask(taskId))
                            },
                            onAddTaskClick = {
                                backStack.add(Route.AddEditTask(null))
                            },
                        )
                    }
                }
                is Route.AddEditTask -> {
                    NavEntry(key) {
                        val viewModel =
                            hiltViewModel<AddEditTaskViewModel, AddEditTaskViewModel.Factory>(
                                creationCallback = { factory ->
                                    factory.create(key)
                                },
                            )
                        AddEditTaskScreen(
                            viewModel = viewModel,
                            onNavigateBack = { backStack.removeLastOrNull() },
                            onTaskSaved = { backStack.removeLastOrNull() },
                        )
                    }
                }
                else -> error("Unknown key: $key")
            }
        },
    )
}
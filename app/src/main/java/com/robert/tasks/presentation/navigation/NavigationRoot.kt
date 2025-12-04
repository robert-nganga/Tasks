package com.robert.tasks.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.robert.tasks.presentation.screens.AddEditTaskScreen
import com.robert.tasks.presentation.screens.TaskListScreen
import com.robert.tasks.presentation.viewmodels.TaskListViewModel
import kotlin.math.hypot

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val  backStack = rememberNavBackStack(Route.Tasks)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when (key) {
                is Route.Tasks -> {
                    NavEntry(key){
                        val viewModel: TaskListViewModel = hiltViewModel()
                        TaskListScreen(
                            onTaskClick = { taskId ->
                                backStack.add(Route.AddEditTask(taskId))
                            }
                        )
                    }
                }
                is Route.AddEditTask -> {
                    NavEntry(key){
                        AddEditTaskScreen(
                            taskIId = key.taskId
                        )
                    }
                }
                else -> error("Unknown key: $key")
            }

        }
    )

}
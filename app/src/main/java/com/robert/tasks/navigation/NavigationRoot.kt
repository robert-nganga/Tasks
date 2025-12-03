package com.robert.tasks.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.robert.tasks.screens.AddEditTaskScreen
import com.robert.tasks.screens.TaskListScreen

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val  backStack = rememberNavBackStack(Route.Tasks)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Route.Tasks -> {
                    NavEntry(key){
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
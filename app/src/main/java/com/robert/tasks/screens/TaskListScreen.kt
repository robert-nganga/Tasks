package com.robert.tasks.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robert.tasks.viewmodels.TaskListViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp


@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = viewModel(),
    onTaskClick: (Int) -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {

        items(tasks.size) { index ->
            val task = tasks[index]
            TaskListItem(
                taskName = task.title,
                onClick = {
                    onTaskClick(task.id)
                }
            )
        }
    }

}

@Composable
fun TaskListItem(
    taskName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = taskName,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )

}
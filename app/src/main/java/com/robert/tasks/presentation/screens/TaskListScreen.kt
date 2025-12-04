package com.robert.tasks.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robert.tasks.presentation.viewmodels.TaskListViewModel


@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = hiltViewModel(),
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
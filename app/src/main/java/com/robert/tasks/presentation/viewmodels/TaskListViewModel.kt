package com.robert.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.robert.tasks.domain.models.TaskSample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskListViewModel : ViewModel() {

    private val _tasks = MutableStateFlow(
        (1..100).map{
            TaskSample(
                id = it,
                title = "Task #$it",
                isCompleted = false
            )
        }
    )
    val tasks = _tasks.asStateFlow()
}
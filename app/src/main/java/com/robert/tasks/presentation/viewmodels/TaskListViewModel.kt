package com.robert.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robert.tasks.domain.repositories.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel
    @Inject
    constructor(
        private val taskRepository: TaskRepository,
    ) : ViewModel() {
        private val _isRefreshing = MutableStateFlow(false)
        val isRefreshing = _isRefreshing.asStateFlow()

        val tasks =
            taskRepository
                .observeTasks()
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 2000L),
                    initialValue = emptyList(),
                )

        init {
            refreshTasks()
        }

        fun refreshTasks() =
            viewModelScope.launch {
                _isRefreshing.value = true
                val result = taskRepository.refreshTasks()
                _isRefreshing.value = false
                if (result.isFailure) {
                    println("Failed to refresh tasks: ${result.exceptionOrNull()}")
                }
            }
    }
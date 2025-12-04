package com.robert.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddEditTaskViewModel(
    private val taskId: Int?
): ViewModel() {

    private val _state = MutableStateFlow(taskId ?: -1)
    val state = _state.asStateFlow()

    init {
        println("AddEditTaskViewModel initialized with taskId: $taskId")
    }

    override fun onCleared() {
        super.onCleared()
        println("AddEditTaskViewModel with taskId: $taskId is cleared")
    }
}
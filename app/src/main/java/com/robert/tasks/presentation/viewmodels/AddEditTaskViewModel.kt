package com.robert.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robert.tasks.domain.repositories.TaskRepository
import com.robert.tasks.presentation.screens.AddEditTaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {


    private val _state = MutableStateFlow(AddEditTaskState())
    val state = _state.asStateFlow()


    fun getTask(id: Int) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        val result = taskRepository.getTask(id)
        _state.update { it.copy(isLoading = false) }
        if (result.isSuccess){
            val task = result.getOrNull()
            task?.let {
                _state.value = state.value.copy(
                    title = it.title,
                    description = it.description,
                    dueDate = it.dueDate,
                    isCompleted = it.isCompleted,
                    fileUrl = it.fileUrl
                )
            }
        } else {
            println("Failed to load task: ${result.exceptionOrNull()}")
        }
    }


    fun onTitleChange(newTitle: String) {
        _state.update { it.copy(title = newTitle) }
    }

    fun onDueDateChange(newDueDate: String) {
        _state.update { it.copy(dueDate = newDueDate) }
    }

    fun onDescriptionChange(newDescription: String) {
        _state.update { it.copy(description = newDescription) }
    }

    fun onIsCompletedChange(newIsCompleted: Boolean) {
        _state.update { it.copy(isCompleted = newIsCompleted) }
    }

}
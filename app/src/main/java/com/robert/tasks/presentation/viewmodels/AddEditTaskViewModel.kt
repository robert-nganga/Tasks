package com.robert.tasks.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.UpdateTaskRequest
import com.robert.tasks.domain.repositories.TaskRepository
import com.robert.tasks.presentation.navigation.Route
import com.robert.tasks.presentation.screens.AddEditTaskState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddEditTaskViewModel.Factory::class)
class AddEditTaskViewModel
    @AssistedInject
    constructor(
        private val taskRepository: TaskRepository,
        @Assisted private val navKey: Route.AddEditTask,
    ) : ViewModel() {
        private val _state = MutableStateFlow(AddEditTaskState())
        val state = _state.asStateFlow()

        val taskId: Int?
            get() = navKey.taskId

        @AssistedFactory
        interface Factory {
            fun create(navKey: Route.AddEditTask): AddEditTaskViewModel
        }

        init {
            navKey.taskId?.let {
                getTask(it)
            }
        }

        fun saveTask() =
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                val result =
                    if (taskId != null) {
                        // Update existing task
                        val updateRequest =
                            UpdateTaskRequest(
                                title = state.value.title,
                                description = state.value.description,
                                dueDate = state.value.dueDate,
                                isCompleted = state.value.isCompleted,
                                fileUrl = state.value.fileUrl,
                            )
                        taskRepository.updateTask(taskId!!, updateRequest)
                    } else {
                        // Create new task
                        val createRequest =
                            CreateTaskRequest(
                                title = state.value.title,
                                description = state.value.description,
                                dueDate = state.value.dueDate,
                                fileUrl = state.value.fileUrl,
                            )
                        taskRepository.createTask(createRequest)
                    }

                _state.update { it.copy(isLoading = false) }

                if (result.isFailure) {
                    println("Failed to save task: ${result.exceptionOrNull()}")
                }
            }

        fun deleteTask() =
            viewModelScope.launch {
                taskId ?: return@launch

                _state.update { it.copy(isLoading = true) }
                val result = taskRepository.deleteTask(taskId!!)
                _state.update { it.copy(isLoading = false) }

                if (result.isFailure) {
                    println("Failed to delete task: ${result.exceptionOrNull()}")
                }
            }

        fun getTask(id: Int) =
            viewModelScope.launch {
                println("Loading task with id: $id")
                _state.update { it.copy(isLoading = true) }
                val result = taskRepository.getTask(id)
                _state.update { it.copy(isLoading = false) }
                if (result.isSuccess) {
                    val task = result.getOrNull()
                    task?.let {
                        _state.value =
                            state.value.copy(
                                title = it.title,
                                description = it.description,
                                dueDate = it.dueDate,
                                isCompleted = it.isCompleted,
                                fileUrl = it.fileUrl,
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
package com.robert.tasks.presentation.screens

data class AddEditTaskState(
    val isLoading: Boolean = false,
    val title: String = "",
    val dueDate: String = "",
    val description: String = "",
    val isCompleted: Boolean = false,
    val fileUrl: String? = null,
)

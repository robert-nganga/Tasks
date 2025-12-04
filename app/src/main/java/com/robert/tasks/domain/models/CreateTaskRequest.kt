package com.robert.tasks.domain.models

data class CreateTaskRequest(
    val title: String,
    val dueDate: String,
    val description: String,
    val isCompleted: Boolean = false,
    val fileUrl: String? = null
)
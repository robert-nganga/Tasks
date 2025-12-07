package com.robert.tasks.domain.models

data class UpdateTaskRequest(
    val title: String?,
    val dueDate: String?,
    val description: String?,
    val isCompleted: Boolean?,
    val fileUrl: String?,
)
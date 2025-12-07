package com.robert.tasks.data.remote.dtos

data class TaskResponse(
    val id: Int,
    val title: String,
    val dueDate: String,
    val description: String,
    val isCompleted: Boolean,
    val fileUrl: String?,
)
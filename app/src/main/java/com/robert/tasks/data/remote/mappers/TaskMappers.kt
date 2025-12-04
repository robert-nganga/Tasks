package com.robert.tasks.data.remote.mappers

import com.robert.tasks.data.remote.dtos.TaskResponse
import com.robert.tasks.domain.models.Task

fun TaskResponse.toDomainModel() = Task(
    id = this.id,
    title = this.title,
    dueDate = this.dueDate,
    description = this.description,
    isCompleted = this.isCompleted,
    fileUrl = this.fileUrl
)
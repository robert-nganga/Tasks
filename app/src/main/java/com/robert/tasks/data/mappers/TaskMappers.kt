package com.robert.tasks.data.mappers

import com.robert.tasks.data.local.entities.TaskEntity
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

fun TaskResponse.toEntity() = TaskEntity(
    id = this.id,
    title = this.title,
    dueDate = this.dueDate,
    description = this.description,
    isCompleted = this.isCompleted,
    fileUrl = this.fileUrl
)

fun TaskEntity.toDomainModel() = Task(
    id = this.id,
    title = this.title,
    dueDate = this.dueDate,
    description = this.description,
    isCompleted = this.isCompleted,
    fileUrl = this.fileUrl
)

fun Task.toEntity() = TaskEntity(
    id = this.id,
    title = this.title,
    dueDate = this.dueDate,
    description = this.description,
    isCompleted = this.isCompleted,
    fileUrl = this.fileUrl
)
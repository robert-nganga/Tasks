package com.robert.tasks.domain.repositories

import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.Task

interface TaskRepository {
    suspend fun getLocalTasks(): Result<List<Task>>
    suspend fun createTask(task: CreateTaskRequest): Result<Task>
}
package com.robert.tasks.domain.repositories

import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.Task
import com.robert.tasks.domain.models.UpdateTaskRequest
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>

    suspend fun refreshTasks(): Result<Unit>

    suspend fun getTask(id: Int): Result<Task>

    suspend fun deleteTask(id: Int): Result<Unit>

    suspend fun updateTask(
        id: Int,
        task: UpdateTaskRequest,
    ): Result<Task>

    suspend fun createTask(task: CreateTaskRequest): Result<Task>
}
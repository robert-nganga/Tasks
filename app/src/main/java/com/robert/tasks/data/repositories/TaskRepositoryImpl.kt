package com.robert.tasks.data.repositories

import com.robert.tasks.data.local.dao.TaskDao
import com.robert.tasks.data.remote.services.TaskService
import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.Task
import com.robert.tasks.domain.repositories.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskService: TaskService
): TaskRepository {
    override suspend fun getLocalTasks(): Result<List<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun createTask(task: CreateTaskRequest): Result<Task> {
        TODO("Not yet implemented")
    }
}
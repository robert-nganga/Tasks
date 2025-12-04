package com.robert.tasks.data.repositories

import com.robert.tasks.data.local.dao.TaskDao
import com.robert.tasks.data.mappers.toDomainModel
import com.robert.tasks.data.mappers.toEntity
import com.robert.tasks.data.remote.dtos.TaskResponse
import com.robert.tasks.data.remote.services.TaskService
import com.robert.tasks.di.IoDispatcher
import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.Task
import com.robert.tasks.domain.models.UpdateTaskRequest
import com.robert.tasks.domain.repositories.TaskRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val taskService: TaskService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
): TaskRepository {



    override fun observeTasks(): Flow<List<Task>> {
        return taskDao.observeAllTasks()
            .map { entities -> entities.map { it.toDomainModel() } }
            .flowOn(dispatcher)
    }

    override suspend fun refreshTasks(): Result<Unit> = withContext(dispatcher) {
        try {
            val response = taskService.getTasks()
            val tasksResponse: List<TaskResponse>? = response.body()
            if (response.isSuccessful && tasksResponse != null) {
                val tasks = tasksResponse.map { it.toEntity() }
                // Does not create duplicates due to OnConflictStrategy.REPLACE
                taskDao.insertAllTasks(*tasks.toTypedArray())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch tasks: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTask(id: Int): Result<Task> = withContext(dispatcher) {
        try {
            taskDao.getTaskById(id)?.let {
                return@withContext Result.success(it.toDomainModel())
            }

            val response = taskService.getTask(id)
            val taskResponse = response.body()
            if (response.isSuccessful && taskResponse != null) {
                val task = taskResponse.toDomainModel()
                taskDao.insertTask(taskResponse.toEntity())
                Result.success(task)
            } else {
                Result.failure(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(id: Int): Result<Unit> = withContext(dispatcher) {
        try {
            val response = taskService.deleteTask(id)
            if (response.isSuccessful) {
                taskDao.deleteTaskById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(
        id: Int,
        task: UpdateTaskRequest
    ): Result<Task> = withContext(dispatcher) {
            try {
                val response = taskService.updateTask(id, task)
                val taskResponse = response.body()
                if (response.isSuccessful && taskResponse != null) {
                    taskDao.updateTask(taskResponse.toEntity())
                    Result.success(taskResponse.toDomainModel())
                } else {
                    Result.failure(Exception("Failed to update task: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun createTask(task: CreateTaskRequest): Result<Task> = withContext(dispatcher) {
        try {
            val response = taskService.createTask(task)
            val taskResponse = response.body()
            if (response.isSuccessful && taskResponse != null) {
                taskDao.insertTask(taskResponse.toEntity())
                Result.success(taskResponse.toDomainModel())
            } else {
                Result.failure(Exception("Failed to create task: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
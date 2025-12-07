package com.robert.tasks.data.repositories

import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.Task
import com.robert.tasks.domain.models.UpdateTaskRequest
import com.robert.tasks.domain.repositories.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class FakeTaskRepository : TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private var shouldReturnError = false
    private var nextId = 1


    fun setShouldReturnError(value: Boolean) {
        shouldReturnError = value
    }


    fun addTasks(vararg tasksToAdd: Task) {
        tasks.addAll(tasksToAdd)
        tasksFlow.value = tasks.toList()
    }

    fun clearTasks() {
        tasks.clear()
        tasksFlow.value = emptyList()
        nextId = 1
    }

    fun getTasksCount(): Int = tasks.size

    override fun observeTasks(): Flow<List<Task>> = tasksFlow

    override suspend fun refreshTasks(): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Failed to refresh tasks"))
        } else {
            tasksFlow.value = tasks.toList()
            Result.success(Unit)
        }
    }

    override suspend fun getTask(id: Int): Result<Task> {
        return if (shouldReturnError) {
            Result.failure(Exception("Failed to get task"))
        } else {
            val task = tasks.find { it.id == id }
            if (task != null) {
                Result.success(task)
            } else {
                Result.failure(Exception("Task not found"))
            }
        }
    }

    override suspend fun deleteTask(id: Int): Result<Unit> {
        return if (shouldReturnError) {
            Result.failure(Exception("Failed to delete task"))
        } else {
            val removed = tasks.removeIf { it.id == id }
            if (removed) {
                tasksFlow.value = tasks.toList()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Task not found"))
            }
        }
    }

    override suspend fun updateTask(id: Int, task: UpdateTaskRequest): Result<Task> {
        return if (shouldReturnError) {
            Result.failure(Exception("Failed to update task"))
        } else {
            val index = tasks.indexOfFirst { it.id == id }
            if (index != -1) {
                val existingTask = tasks[index]
                val updatedTask = existingTask.copy(
                    title = task.title ?: existingTask.title,
                    description = task.description ?: existingTask.description,
                    dueDate = task.dueDate ?: existingTask.dueDate,
                    isCompleted = task.isCompleted ?: existingTask.isCompleted,
                    fileUrl = task.fileUrl
                )
                tasks[index] = updatedTask
                tasksFlow.value = tasks.toList()
                Result.success(updatedTask)
            } else {
                Result.failure(Exception("Task not found"))
            }
        }
    }

    override suspend fun createTask(task: CreateTaskRequest): Result<Task> {
        return if (shouldReturnError) {
            Result.failure(Exception("Failed to create task"))
        } else {
            val newTask = Task(
                id = nextId++,
                title = task.title,
                description = task.description,
                dueDate = task.dueDate,
                isCompleted = false,
                fileUrl = task.fileUrl,
            )
            tasks.add(newTask)
            tasksFlow.value = tasks.toList()
            Result.success(newTask)
        }
    }
}

// Test data builders for convenience
object TaskTestData {
    fun createTask(
        id: Int = 1,
        title: String = "Test Task",
        description: String = "Test Description",
        dueDate: String = "2024-12-31",
        isCompleted: Boolean = false,
        fileUrl: String? = null
    ) = Task(
        id = id,
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted,
        fileUrl = fileUrl,
    )

    fun createTaskRequest(
        title: String = "New Task",
        description: String = "New Description",
        dueDate: String = "2024-12-31",
        fileUrl: String? = null
    ) = CreateTaskRequest(
        title = title,
        description = description,
        dueDate = dueDate,
        fileUrl = fileUrl
    )

    fun updateTaskRequest(
        title: String = "Updated Task",
        description: String = "Updated Description",
        dueDate: String = "2024-12-31",
        isCompleted: Boolean = false,
        fileUrl: String? = null
    ) = UpdateTaskRequest(
        title = title,
        description = description,
        dueDate = dueDate,
        isCompleted = isCompleted,
        fileUrl = fileUrl
    )
}
package com.robert.tasks.data.mappers

import com.robert.tasks.data.local.entities.TaskEntity
import com.robert.tasks.data.remote.dtos.TaskResponse
import com.robert.tasks.domain.models.Task
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskMapperTest {

    @Test
    fun `TaskEntity toDomainModel`() {
        val taskEntity = TaskEntity(
            id = 1,
            title = "Test Title",
            dueDate = "2025-01-01T12:00:00Z",
            description = "Test Description",
            isCompleted = false,
            fileUrl = "http://example.com/file.jpg"
        )

        val task = taskEntity.toDomainModel()

        assertEquals(1, task.id)
        assertEquals("Test Title", task.title)
        assertEquals("2025-01-01T12:00:00Z", task.dueDate)
        assertEquals("Test Description", task.description)
        assertEquals(false, task.isCompleted)
        assertEquals("http://example.com/file.jpg", task.fileUrl)
    }

    @Test
    fun `TaskResponse toDomainModel`() {
        val taskResponse = TaskResponse(
            id = 1,
            title = "Test Title",
            dueDate = "2025-01-01T12:00:00Z",
            description = "Test Description",
            isCompleted = false,
            fileUrl = "http://example.com/file.jpg"
        )

        val task = taskResponse.toDomainModel()

        assertEquals(1, task.id)
        assertEquals("Test Title", task.title)
        assertEquals("2025-01-01T12:00:00Z", task.dueDate)
        assertEquals("Test Description", task.description)
        assertEquals(false, task.isCompleted)
        assertEquals("http://example.com/file.jpg", task.fileUrl)
    }

    @Test
    fun `TaskResponse toEntity`() {
        val taskResponse = TaskResponse(
            id = 1,
            title = "Test Title",
            dueDate = "2025-01-01T12:00:00Z",
            description = "Test Description",
            isCompleted = false,
            fileUrl = "http://example.com/file.jpg"
        )

        val taskEntity = taskResponse.toEntity()

        assertEquals(1, taskEntity.id)
        assertEquals("Test Title", taskEntity.title)
        assertEquals("2025-01-01T12:00:00Z", taskEntity.dueDate)
        assertEquals("Test Description", taskEntity.description)
        assertEquals(false, taskEntity.isCompleted)
        assertEquals("http://example.com/file.jpg", taskEntity.fileUrl)
    }

    @Test
    fun `Task toEntity`() {
        val task = Task(
            id = 1,
            title = "Test Title",
            dueDate = "2025-01-01T12:00:00Z",
            description = "Test Description",
            isCompleted = false,
            fileUrl = "http://example.com/file.jpg"
        )

        val taskEntity = task.toEntity()

        assertEquals(1, taskEntity.id)
        assertEquals("Test Title", taskEntity.title)
        assertEquals("2025-01-01T12:00:00Z", taskEntity.dueDate)
        assertEquals("Test Description", taskEntity.description)
        assertEquals(false, taskEntity.isCompleted)
        assertEquals("http://example.com/file.jpg", taskEntity.fileUrl)
    }
}

package com.robert.tasks.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.robert.tasks.domain.models.Task
import com.robert.tasks.domain.repositories.TaskRepository
import com.robert.tasks.presentation.navigation.Route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AddEditTaskViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var taskRepository: TaskRepository

    private lateinit var viewModel: AddEditTaskViewModel

    @Test
    fun `getTask should load task into state when taskId is not null`() =
        runTest {
            val task = Task(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null)
            coEvery { taskRepository.getTask(1) } returns Result.success(task)
            val navKey = Route.AddEditTask(1)

            viewModel = AddEditTaskViewModel(taskRepository, navKey)

            assertEquals("Task 1", viewModel.state.value.title)
            assertEquals("Description 1", viewModel.state.value.description)
        }

    @Test
    fun `saveTask should call createTask when taskId is null`() =
        runTest {
            val navKey = Route.AddEditTask(null)
            viewModel = AddEditTaskViewModel(taskRepository, navKey)
            viewModel.onTitleChange("New Task")
            viewModel.onDescriptionChange("New Desc")
            viewModel.onDueDateChange("2025-01-01T12:00:00Z")

            viewModel.saveTask()

            coVerify { taskRepository.createTask(any()) }
        }

    @Test
    fun `saveTask should call updateTask when taskId is not null`() =
        runTest {
            val task = Task(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null)
            coEvery { taskRepository.getTask(1) } returns Result.success(task)
            val navKey = Route.AddEditTask(1)
            viewModel = AddEditTaskViewModel(taskRepository, navKey)

            viewModel.saveTask()

            coVerify { taskRepository.updateTask(1, any()) }
        }

    @Test
    fun `deleteTask should call deleteTask from repository`() =
        runTest {
            val task = Task(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null)
            coEvery { taskRepository.getTask(1) } returns Result.success(task)
            val navKey = Route.AddEditTask(1)
            viewModel = AddEditTaskViewModel(taskRepository, navKey)

            viewModel.deleteTask()

            coVerify { taskRepository.deleteTask(1) }
        }

    @Test
    fun `onTitleChange should update state`() {
        val navKey = Route.AddEditTask(null)
        viewModel = AddEditTaskViewModel(taskRepository, navKey)
        viewModel.onTitleChange("New Title")
        assertEquals("New Title", viewModel.state.value.title)
    }

    @Test
    fun `onDescriptionChange should update state`() {
        val navKey = Route.AddEditTask(null)
        viewModel = AddEditTaskViewModel(taskRepository, navKey)
        viewModel.onDescriptionChange("New Desc")
        assertEquals("New Desc", viewModel.state.value.description)
    }

    @Test
    fun `onDueDateChange should update state`() {
        val navKey = Route.AddEditTask(null)
        viewModel = AddEditTaskViewModel(taskRepository, navKey)
        viewModel.onDueDateChange("2025-01-01T12:00:00Z")
        assertEquals("2025-01-01T12:00:00Z", viewModel.state.value.dueDate)
    }

    @Test
    fun `onIsCompletedChange should update state`() {
        val navKey = Route.AddEditTask(null)
        viewModel = AddEditTaskViewModel(taskRepository, navKey)
        viewModel.onIsCompletedChange(true)
        assertTrue(viewModel.state.value.isCompleted)
    }
}

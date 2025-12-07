package com.robert.tasks.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.robert.tasks.domain.models.Task
import com.robert.tasks.domain.repositories.TaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskListViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var taskRepository: TaskRepository

    private lateinit var viewModel: TaskListViewModel

    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

    @Before
    fun setUp() {
        // No-op, ViewModel will be initialized in each test within runTest
    }

    @Test
    fun `tasks flow should emit tasks from repository`() =
        runTest {
            val tasks =
                listOf(
                    Task(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null),
                    Task(2, "Task 2", "2025-01-02T12:00:00Z", "Description 2", true, null),
                )
            coEvery { taskRepository.observeTasks() } returns tasksFlow
            coEvery { taskRepository.refreshTasks() } returns Result.success(Unit)
            viewModel = TaskListViewModel(taskRepository)

            viewModel.tasks.test {
                tasksFlow.emit(tasks) // Emit after ViewModel initialization
                val emittedTasks = awaitItem()
                assertEquals(2, emittedTasks.size)
                assertEquals("Task 1", emittedTasks[0].title)
                assertEquals("Task 2", emittedTasks[1].title)
            }
        }

    @Test
    fun `isRefreshing should be true while refreshing and false after`() =
        runTest {
            coEvery { taskRepository.observeTasks() } returns flowOf(emptyList()) // Mock observeTasks
            coEvery { taskRepository.refreshTasks() } returns Result.success(Unit)
            viewModel = TaskListViewModel(taskRepository) // Initialize ViewModel here

            viewModel.isRefreshing.test {
                // Initial state
                assertEquals(false, awaitItem())

                // Trigger refresh (it's called in init, but we can call it again for explicit testing)
                viewModel.refreshTasks()

                // Should be true during refresh
                assertEquals(true, awaitItem())

                // Should be false after refresh completes
                assertEquals(false, awaitItem())
            }
            coVerify(atLeast = 1) { taskRepository.refreshTasks() }
        }

    @Test
    fun `refreshTasks should update tasks flow`() =
        runTest {
            val initialTasks = listOf(Task(1, "Initial", "2025-01-01T12:00:00Z", "Desc", false, null))
            val refreshedTasks = listOf(Task(3, "Refreshed", "2025-01-03T12:00:00Z", "Desc", false, null))

            coEvery { taskRepository.observeTasks() } returns tasksFlow
            coEvery { taskRepository.refreshTasks() } coAnswers {
                tasksFlow.emit(refreshedTasks)
                Result.success(Unit)
            }
            viewModel = TaskListViewModel(taskRepository)

            viewModel.tasks.test {
                tasksFlow.emit(initialTasks) // Emit after ViewModel initialization
                assertEquals(initialTasks, awaitItem()) // Initial emission

                viewModel.refreshTasks()

                assertEquals(refreshedTasks, awaitItem()) // Refreshed emission
            }
        }
}
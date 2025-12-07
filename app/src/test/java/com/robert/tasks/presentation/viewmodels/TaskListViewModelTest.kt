package com.robert.tasks.presentation.viewmodels

import app.cash.turbine.test
import com.robert.tasks.data.repositories.FakeTaskRepository
import com.robert.tasks.data.repositories.TaskTestData
import com.robert.tasks.domain.models.Task
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty task list`() = runTest {
        // When
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // Then
        assertEquals(emptyList<Task>(), viewModel.tasks.value)
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `observes tasks from repository`() = runTest {
        // Given
        val task1 = TaskTestData.createTask(id = 1, title = "Task 1")
        val task2 = TaskTestData.createTask(id = 2, title = "Task 2")
        fakeRepository.addTasks(task1, task2)

        // When
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        viewModel.tasks.test {
            val tasks = awaitItem()
            assertEquals(2, tasks.size)
            assertEquals("Task 1", tasks[0].title)
            assertEquals("Task 2", tasks[1].title)
        }
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
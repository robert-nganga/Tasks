package com.robert.tasks.presentation.viewmodels

import app.cash.turbine.test
import com.robert.tasks.data.repositories.FakeTaskRepository
import com.robert.tasks.data.repositories.TaskTestData
import com.robert.tasks.domain.models.Task
import io.mockk.junit4.MockKRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var viewModel: TaskListViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeTaskRepository()
    }

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
    fun `tasks flow emits updates when repository changes`() = runTest {
        // Given
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // When/Then
        viewModel.tasks.test {
            // Initial empty list
            assertEquals(emptyList<Task>(), awaitItem())

            // Add a task
            val task = TaskTestData.createTask(id = 1, title = "New Task")
            fakeRepository.addTasks(task)
            advanceUntilIdle()

            // Should emit updated list
            val tasks = awaitItem()
            assertEquals(1, tasks.size)
            assertEquals("New Task", tasks[0].title)
        }
    }

    @Test
    fun `refreshTasks calls repository refresh`() = runTest {
        // Given
        val task1 = TaskTestData.createTask(id = 1, title = "Task 1")
        fakeRepository.addTasks(task1)
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // When
        viewModel.refreshTasks()
        advanceUntilIdle()

        viewModel.tasks.test {
            val tasks = awaitItem()
            assertEquals(1, tasks.size)
        }

    }


    @Test
    fun `handles repository refresh failure gracefully`() = runTest {
        // Given
        fakeRepository.setShouldReturnError(true)

        // When
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // Then - should not crash and refreshing should be false
        assertFalse(viewModel.isRefreshing.value)
        assertEquals(emptyList<Task>(), viewModel.tasks.value)
    }

    @Test
    fun `tasks flow reflects task additions`() = runTest {
        // Given
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // When
        val task1 = TaskTestData.createTask(id = 1, title = "Task 1")
        fakeRepository.addTasks(task1)
        advanceUntilIdle()

        val task2 = TaskTestData.createTask(id = 2, title = "Task 2")
        fakeRepository.addTasks(task2)
        advanceUntilIdle()

        viewModel.tasks.test {
            val tasks = awaitItem()
            assertEquals(2, tasks.size)
        }
    }

    @Test
    fun `tasks flow reflects task updates`() = runTest {
        // Given
        val task = TaskTestData.createTask(id = 1, title = "Original Title")
        fakeRepository.addTasks(task)
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // When
        val updateRequest = TaskTestData.updateTaskRequest(title = "Updated Title")
        fakeRepository.updateTask(1, updateRequest)
        advanceUntilIdle()

        viewModel.tasks.test {
            val tasks = awaitItem()
            assertEquals(1, tasks.size)
            assertEquals("Updated Title", tasks[0].title)
        }
    }

    @Test
    fun `tasks flow reflects task deletions`() = runTest {
        // Given
        val task1 = TaskTestData.createTask(id = 1, title = "Task 1")
        val task2 = TaskTestData.createTask(id = 2, title = "Task 2")
        fakeRepository.addTasks(task1, task2)
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // When
        fakeRepository.deleteTask(1)
        advanceUntilIdle()

        viewModel.tasks.test {
            val tasks = awaitItem()
            assertEquals(1, tasks.size)
            assertEquals("Task 2", tasks[0].title)
        }
    }

    @Test
    fun `refreshing state returns to false even on repository error`() = runTest {
        // Given
        viewModel = TaskListViewModel(fakeRepository)
        advanceUntilIdle()

        // When
        fakeRepository.setShouldReturnError(true)
        viewModel.refreshTasks()
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isRefreshing.value)
    }

}
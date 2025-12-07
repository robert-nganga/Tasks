import app.cash.turbine.test
import com.robert.tasks.data.repositories.FakeTaskRepository
import com.robert.tasks.data.repositories.TaskTestData
import com.robert.tasks.presentation.navigation.Route
import com.robert.tasks.presentation.viewmodels.AddEditTaskViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTaskViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty when creating new task`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)

        // When
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // Then
        val state = viewModel.state.value
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertEquals("", state.dueDate)
        assertFalse(state.isCompleted)
        assertNull(state.fileUrl)
        assertFalse(state.isLoading)
    }

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
    fun `deleteTask does nothing when taskId is null`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // When
        viewModel.deleteTask()
        advanceUntilIdle()

        // Then
        assertEquals(0, fakeRepository.getTasksCount())
    }

    @Test
    fun `handles repository failure on getTask`() = runTest {
        // Given
        fakeRepository.setShouldReturnError(true)
        val navKey = Route.AddEditTask(taskId = 1)

        // When
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        advanceUntilIdle()

        // Then - state should remain with default values
        val state = viewModel.state.value
        assertEquals("", state.title)
        assertFalse(state.isLoading)
    }

    @Test
    fun `handles repository failure on saveTask`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        fakeRepository.setShouldReturnError(true)

        viewModel.onTitleChange("Test")

        // When
        viewModel.saveTask()
        advanceUntilIdle()

        // Then - should complete without crashing
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(0, fakeRepository.getTasksCount()) // Task wasn't created
    }

    @Test
    fun `handles repository failure on deleteTask`() = runTest {
        // Given
        val task = TaskTestData.createTask(id = 1)
        fakeRepository.addTasks(task)

        val navKey = Route.AddEditTask(taskId = 1)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        advanceUntilIdle()

        fakeRepository.setShouldReturnError(true)

        // When
        viewModel.deleteTask()
        advanceUntilIdle()

        // Then - task should still exist
        assertEquals(1, fakeRepository.getTasksCount())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `multiple state changes are reflected correctly`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // When
        viewModel.onTitleChange("Title 1")
        viewModel.onTitleChange("Title 2")
        viewModel.onDescriptionChange("Description")
        viewModel.onDueDateChange("2024-12-31")
        viewModel.onIsCompletedChange(true)

        // Then
        val state = viewModel.state.value
        assertEquals("Title 2", state.title)
        assertEquals("Description", state.description)
        assertEquals("2024-12-31", state.dueDate)
        assertTrue(state.isCompleted)
    }

    @Test
    fun `file url is preserved during update`() = runTest {
        // Given
        val task = TaskTestData.createTask(
            id = 1,
            fileUrl = "http://example.com/file.pdf"
        )
        fakeRepository.addTasks(task)

        val navKey = Route.AddEditTask(taskId = 1)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        advanceUntilIdle()

        viewModel.onTitleChange("Updated Title")

        // When
        viewModel.saveTask()
        advanceUntilIdle()

        // Then
        val updatedTask = fakeRepository.getTask(1).getOrNull()
        assertEquals("http://example.com/file.pdf", updatedTask?.fileUrl)
    }
}
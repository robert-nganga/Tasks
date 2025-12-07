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
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTaskViewModelTest {
    private lateinit var fakeRepository: FakeTaskRepository
    private lateinit var viewModel: AddEditTaskViewModel
    private val testDispatcher = StandardTestDispatcher()

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
    fun `loads existing task when taskId is provided`() = runTest {
        // Given
        val existingTask = TaskTestData.createTask(
            id = 1,
            title = "Existing Task",
            description = "Existing Description",
            dueDate = "2024-12-31",
            fileUrl = "http://example.com/file.pdf"
        )
        fakeRepository.addTasks(existingTask)

        val navKey = Route.AddEditTask(taskId = 1)

        // When
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals("Existing Task", state.title)
        assertEquals("Existing Description", state.description)
        assertEquals("2024-12-31", state.dueDate)
        assertFalse(state.isCompleted)
        assertEquals("http://example.com/file.pdf", state.fileUrl)
        assertFalse(state.isLoading)
    }

    @Test
    fun `loading state is set during task fetch`() = runTest {
        // Given
        val task = TaskTestData.createTask(id = 1)
        fakeRepository.addTasks(task)
        val navKey = Route.AddEditTask(taskId = 1)

        // When
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // Then
        viewModel.state.test {
            // Initial state
            val initialState = awaitItem()
            assertFalse(initialState.isLoading)

            advanceUntilIdle()

            // Final state after loading
            val finalState = expectMostRecentItem()
            assertFalse(finalState.isLoading)
            assertEquals("Test Task", finalState.title)
        }
    }

    @Test
    fun `onTitleChange updates title in state`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // When
        viewModel.onTitleChange("New Title")

        // Then
        assertEquals("New Title", viewModel.state.value.title)
    }

    @Test
    fun `onDescriptionChange updates description in state`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // When
        viewModel.onDescriptionChange("New Description")

        // Then
        assertEquals("New Description", viewModel.state.value.description)
    }

    @Test
    fun `onDueDateChange updates dueDate in state`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // When
        viewModel.onDueDateChange("2024-12-31")

        // Then
        assertEquals("2024-12-31", viewModel.state.value.dueDate)
    }

    @Test
    fun `onIsCompletedChange updates isCompleted in state`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        // When
        viewModel.onIsCompletedChange(true)

        // Then
        assertTrue(viewModel.state.value.isCompleted)
    }

    @Test
    fun `saveTask creates new task when taskId is null`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)

        viewModel.onTitleChange("New Task")
        viewModel.onDescriptionChange("Task Description")
        viewModel.onDueDateChange("2024-12-31")

        // When
        viewModel.saveTask()
        advanceUntilIdle()

        // Then
        assertEquals(1, fakeRepository.getTasksCount())
        val createdTask = fakeRepository.getTask(1).getOrNull()
        assertEquals("New Task", createdTask?.title)
        assertEquals("Task Description", createdTask?.description)
        assertEquals("2024-12-31", createdTask?.dueDate)
    }

    @Test
    fun `saveTask updates existing task when taskId is provided`() = runTest {
        // Given
        val existingTask = TaskTestData.createTask(
            id = 1,
            title = "Old Title",
            description = "Old Description"
        )
        fakeRepository.addTasks(existingTask)

        val navKey = Route.AddEditTask(taskId = 1)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        advanceUntilIdle()

        viewModel.onTitleChange("Updated Title")
        viewModel.onDescriptionChange("Updated Description")

        // When
        viewModel.saveTask()
        advanceUntilIdle()

        // Then
        val updatedTask = fakeRepository.getTask(1).getOrNull()
        assertEquals("Updated Title", updatedTask?.title)
        assertEquals("Updated Description", updatedTask?.description)
        assertEquals(1, fakeRepository.getTasksCount()) // Still only 1 task
    }

    @Test
    fun `saveTask sets loading state correctly`() = runTest {
        // Given
        val navKey = Route.AddEditTask(taskId = null)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        viewModel.onTitleChange("Test")

        // When/Then
        viewModel.state.test {
            skipItems(1) // Skip initial state

            viewModel.saveTask()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            advanceUntilIdle()

            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
        }
    }

    @Test
    fun `deleteTask removes task from repository`() = runTest {
        // Given
        val task = TaskTestData.createTask(id = 1)
        fakeRepository.addTasks(task)

        val navKey = Route.AddEditTask(taskId = 1)
        viewModel = AddEditTaskViewModel(fakeRepository, navKey)
        advanceUntilIdle()

        // When
        viewModel.deleteTask()
        advanceUntilIdle()

        // Then
        assertEquals(0, fakeRepository.getTasksCount())
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
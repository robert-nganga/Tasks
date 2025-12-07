import com.robert.tasks.data.repositories.FakeTaskRepository
import com.robert.tasks.data.repositories.TaskTestData
import com.robert.tasks.presentation.navigation.Route
import com.robert.tasks.presentation.viewmodels.AddEditTaskViewModel
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTaskViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    private lateinit var viewModel: AddEditTaskViewModel
    private val fakeRepository = FakeTaskRepository()


    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
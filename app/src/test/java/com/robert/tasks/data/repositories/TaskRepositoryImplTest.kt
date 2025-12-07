package com.robert.tasks.data.repositories

import app.cash.turbine.test
import com.robert.tasks.data.local.dao.TaskDao
import com.robert.tasks.data.local.entities.TaskEntity
import com.robert.tasks.data.remote.dtos.TaskResponse
import com.robert.tasks.data.remote.services.TaskService
import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.UpdateTaskRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class TaskRepositoryImplTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @RelaxedMockK
    private lateinit var taskDao: TaskDao

    @RelaxedMockK
    private lateinit var taskService: TaskService

    private lateinit var taskRepository: TaskRepositoryImpl

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        taskRepository = TaskRepositoryImpl(taskDao, taskService, testDispatcher)
    }

    @Test
    fun `observeTasks should return flow of tasks from dao`() =
        runTest {
            val taskEntities =
                listOf(
                    TaskEntity(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null),
                    TaskEntity(2, "Task 2", "2025-01-02T12:00:00Z", "Description 2", true, null),
                )
            coEvery { taskDao.observeAllTasks() } returns flowOf(taskEntities)

            taskRepository.observeTasks().test {
                val tasks = awaitItem()
                assertEquals(2, tasks.size)
                assertEquals("Task 1", tasks[0].title)
                assertEquals("Task 2", tasks[1].title)
                awaitComplete()
            }
        }

    @Test
    fun `refreshTasks should fetch from service and insert into dao`() =
        runTest {
            val taskResponses =
                listOf(
                    TaskResponse(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null),
                    TaskResponse(2, "Task 2", "2025-01-02T12:00:00Z", "Description 2", true, null),
                )
            coEvery { taskService.getTasks() } returns Response.success(taskResponses)

            val result = taskRepository.refreshTasks()

            assertTrue(result.isSuccess)
            coVerify { taskDao.insertAllTasks(any(), any()) }
        }

    @Test
    fun `refreshTasks should return failure when service fails`() =
        runTest {
            coEvery { taskService.getTasks() } returns Response.error(404, mockk(relaxed = true))

            val result = taskRepository.refreshTasks()

            assertTrue(result.isFailure)
        }

    @Test
    fun `getTask should return task from dao if exists`() =
        runTest {
            val taskEntity = TaskEntity(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null)
            coEvery { taskDao.getTaskById(1) } returns taskEntity

            val result = taskRepository.getTask(1)

            assertTrue(result.isSuccess)
            assertEquals("Task 1", result.getOrNull()?.title)
            coVerify(exactly = 0) { taskService.getTask(1) }
        }

    @Test
    fun `getTask should fetch from service if not in dao`() =
        runTest {
            val taskResponse = TaskResponse(1, "Task 1", "2025-01-01T12:00:00Z", "Description 1", false, null)
            coEvery { taskDao.getTaskById(1) } returns null
            coEvery { taskService.getTask(1) } returns Response.success(taskResponse)

            val result = taskRepository.getTask(1)

            assertTrue(result.isSuccess)
            assertEquals("Task 1", result.getOrNull()?.title)
            coVerify { taskDao.insertTask(any()) }
        }

    @Test
    fun `deleteTask should delete from service and dao`() =
        runTest {
            coEvery { taskService.deleteTask(1) } returns Response.success(Unit)

            val result = taskRepository.deleteTask(1)

            assertTrue(result.isSuccess)
            coVerify { taskDao.deleteTaskById(1) }
        }

    @Test
    fun `updateTask should update service and dao`() =
        runTest {
            val updateRequest = UpdateTaskRequest("Updated", "Desc", "2025-01-01T12:00:00Z", false, null)
            val taskResponse = TaskResponse(1, "Updated", "2025-01-01T12:00:00Z", "Desc", false, null)
            coEvery { taskService.updateTask(1, updateRequest) } returns Response.success(taskResponse)

            val result = taskRepository.updateTask(1, updateRequest)

            assertTrue(result.isSuccess)
            assertEquals("Updated", result.getOrNull()?.title)
            coVerify { taskDao.updateTask(any()) }
        }

    @Test
    fun `createTask should create in service and dao`() =
        runTest {
            val createRequest = CreateTaskRequest("New Task", "Desc", "2025-01-01T12:00:00Z", null)
            val taskResponse = TaskResponse(1, "New Task", "2025-01-01T12:00:00Z", "Desc", false, null)
            coEvery { taskService.createTask(createRequest) } returns Response.success(taskResponse)

            val result = taskRepository.createTask(createRequest)

            assertTrue(result.isSuccess)
            assertEquals("New Task", result.getOrNull()?.title)
            coVerify { taskDao.insertTask(any()) }
        }
}

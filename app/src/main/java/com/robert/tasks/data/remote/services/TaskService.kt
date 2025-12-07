package com.robert.tasks.data.remote.services

import com.robert.tasks.data.remote.dtos.TaskResponse
import com.robert.tasks.domain.models.CreateTaskRequest
import com.robert.tasks.domain.models.UpdateTaskRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskService {
    @GET("tasks")
    suspend fun getTasks(): Response<List<TaskResponse>>

    @GET("tasks/{id}")
    suspend fun getTask(
        @Path("id") id: Int,
    ): Response<TaskResponse>

    @POST("tasks")
    suspend fun createTask(
        @Body request: CreateTaskRequest,
    ): Response<TaskResponse>

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: Int,
        @Body request: UpdateTaskRequest,
    ): Response<TaskResponse>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: Int,
    ): Response<Unit>
}
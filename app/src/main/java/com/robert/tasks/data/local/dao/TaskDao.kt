package com.robert.tasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.robert.tasks.data.local.entities.TaskEntity


@Dao
interface TaskDao {

    @Query("SELECT * FROM TaskEntity")
    fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM TaskEntity WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): TaskEntity?

    @Insert
    fun insertAllTasks(vararg tasks: TaskEntity)

    @Delete
    fun deleteAllTasks()

}
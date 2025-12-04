package com.robert.tasks.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robert.tasks.data.local.dao.TaskDao
import com.robert.tasks.data.local.entities.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
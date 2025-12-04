package com.robert.tasks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.robert.tasks.data.local.dao.TaskDao
import com.robert.tasks.data.local.entities.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): TaskDao
}
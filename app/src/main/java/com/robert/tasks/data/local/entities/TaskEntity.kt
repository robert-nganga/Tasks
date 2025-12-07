package com.robert.tasks.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    @ColumnInfo(name = "due_date")
    val dueDate: String,
    val description: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "file_url")
    val fileUrl: String?,
)
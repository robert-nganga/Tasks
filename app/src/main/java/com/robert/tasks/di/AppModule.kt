package com.robert.tasks.di

import android.content.Context
import androidx.room.Room
import com.robert.tasks.data.local.dao.TaskDao
import com.robert.tasks.data.local.db.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesTaskDatabase(
        @ApplicationContext applicationContext: Context
    ): TaskDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java, "task_database"
        ).build()
    }

    @Provides
    fun provideTaskDao(db: TaskDatabase): TaskDao {
        return db.taskDao()
    }

}
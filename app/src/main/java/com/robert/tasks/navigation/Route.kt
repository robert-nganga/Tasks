package com.robert.tasks.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route: NavKey {

    @Serializable
    data object Tasks: Route, NavKey

    @Serializable
    data class AddEditTask(val taskId: Int?): Route, NavKey
}
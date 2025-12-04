package com.robert.tasks.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robert.tasks.presentation.viewmodels.AddEditTaskViewModel

@Composable
fun AddEditTaskScreen(
    modifier: Modifier = Modifier,
    taskIId: Int?,
    viewModel: AddEditTaskViewModel = viewModel{
        AddEditTaskViewModel(taskIId)
    },
) {

    Box (
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text("Task #${taskIId ?: "New"}")
    }

}
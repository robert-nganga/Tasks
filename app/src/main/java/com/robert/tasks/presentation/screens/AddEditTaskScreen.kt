package com.robert.tasks.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robert.tasks.presentation.viewmodels.AddEditTaskViewModel
import com.robert.tasks.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: AddEditTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onTaskSaved: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val isEditMode = viewModel.taskId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Task" else "New Task",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    val isButtonEnabled =
                        state.title.isNotBlank() && state.dueDate.isNotBlank() && state.description.isNotBlank() && !state.isLoading

                    IconButton(
                        onClick = {
                            viewModel.saveTask()
                            onTaskSaved()
                        },
                        enabled = isButtonEnabled,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint =
                                if (isButtonEnabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                },
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
            ) {
                // Title Input
                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text("Task Title") },
                    placeholder = { Text("Enter task title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                        ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Due Date Selector
                OutlinedCard(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                    colors =
                        CardDefaults.outlinedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                    shape = RoundedCornerShape(8.dp),
                    border =
                        CardDefaults.outlinedCardBorder().copy(
                            width = 1.dp,
                            brush =
                                androidx.compose.ui.graphics
                                    .SolidColor(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)),
                        ),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Due Date",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            )

                            Text(
                                text =
                                    if (state.dueDate.isNotEmpty()) {
                                        DateUtils.formatToFullDate(state.dueDate)
                                    } else {
                                        "Select due date"
                                    },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color =
                                    if (state.dueDate.isNotEmpty()) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    },
                            )

                            // Show relative time if date is set
                            if (state.dueDate.isNotEmpty()) {
                                Text(
                                    text = DateUtils.getRelativeTimeText(state.dueDate),
                                    fontSize = 12.sp,
                                    color =
                                        when {
                                            DateUtils.isOverdue(state.dueDate) -> Color(0xFFEF4444)
                                            DateUtils.isDueSoon(state.dueDate) -> Color(0xFFF59E0B)
                                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        },
                                )
                            }
                        }

                        if (state.dueDate.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.onDueDateChange("") },
                            ) {
                                Text("Clear", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Input
                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.onDescriptionChange(it) },
                    label = { Text("Description") },
                    placeholder = { Text("Add task description (optional)") },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    maxLines = 6,
                    shape = RoundedCornerShape(8.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                        ),
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Completion Status (only show in edit mode)
                if (isEditMode) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border =
                            CardDefaults.outlinedCardBorder().copy(
                                width = 1.dp,
                                brush =
                                    androidx.compose.ui.graphics.SolidColor(
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    ),
                            ),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "Task Status",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = if (state.isCompleted) "Completed" else "Incomplete",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }

                            Switch(
                                checked = state.isCompleted,
                                onCheckedChange = { viewModel.onIsCompletedChange(it) },
                                colors =
                                    SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF10B981),
                                    ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Attachments Section (if exists)
                if (state.fileUrl != null) {
                    Text(
                        text = "Attachments",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(8.dp),
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp),
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = state.fileUrl?.substringAfterLast("/") ?: "Attachment",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Delete button (only in edit mode)
                if (isEditMode) {
                    OutlinedButton(
                        onClick = {
                            viewModel.deleteTask()
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFEF4444),
                            ),
                        border =
                            ButtonDefaults.outlinedButtonBorder().copy(
                                brush =
                                    androidx.compose.ui.graphics
                                        .SolidColor(Color(0xFFEF4444)),
                            ),
                    ) {
                        Text("Delete Task")
                    }
                }
            }

            // Loading overlay
            if (state.isLoading) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { year, month, day ->
                showDatePicker = false
                showTimePicker = true
                // Store temporarily - we'll combine with time
                viewModel.onDueDateChange("$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}T09:00")
            },
            onDismiss = { showDatePicker = false },
        )
    }

    // Time Picker Dialog (shown after date is selected)
    if (showTimePicker) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                showTimePicker = false
                // Update the date with selected time
                val currentDate = state.dueDate.substringBefore('T')
                viewModel.onDueDateChange("${currentDate}T${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date =
                            java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        onDateSelected(date.year, date.monthValue, date.dayOfMonth)
                    }
                },
            ) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState =
        rememberTimePickerState(
            initialHour = 9,
            initialMinute = 0,
        )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                },
            ) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        },
    )
}
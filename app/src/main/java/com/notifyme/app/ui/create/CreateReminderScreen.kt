package com.notifyme.app.ui.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReminderScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateReminderViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val dateFormat = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val selectedCalendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, state.year)
        set(Calendar.MONTH, state.month)
        set(Calendar.DAY_OF_MONTH, state.day)
        set(Calendar.HOUR_OF_DAY, state.hour)
        set(Calendar.MINUTE, state.minute)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Reminder") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Title") },
                placeholder = { Text("e.g. Clothes wash is done") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.message,
                onValueChange = viewModel::updateMessage,
                label = { Text("Message (optional)") },
                placeholder = { Text("e.g. Take clothes out of the machine") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Text("When", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, y, m, d -> viewModel.updateDate(y, m, d) },
                                state.year,
                                state.month,
                                state.day
                            ).show()
                        }
                ) {
                    Text(
                        text = dateFormat.format(selectedCalendar.time),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(Modifier.padding(4.dp))

                OutlinedCard(
                    modifier = Modifier
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, h, m -> viewModel.updateTime(h, m) },
                                state.hour,
                                state.minute,
                                true
                            ).show()
                        }
                ) {
                    Text(
                        text = timeFormat.format(selectedCalendar.time),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Repeat",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = state.isRecurring,
                    onCheckedChange = viewModel::updateRecurring
                )
            }

            if (state.isRecurring) {
                Spacer(Modifier.height(12.dp))

                val options = listOf("DAILY", "WEEKLY", "CUSTOM")
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = state.recurringType == option,
                            onClick = { viewModel.updateRecurringType(option) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            )
                        ) {
                            Text(option.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }

                if (state.recurringType == "CUSTOM") {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.customIntervalMinutes.toString(),
                        onValueChange = { value ->
                            value.toLongOrNull()?.let { viewModel.updateCustomInterval(it) }
                        },
                        label = { Text("Interval (minutes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.save(onNavigateBack) },
                enabled = state.isValid && !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Saving..." else "Create Reminder")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

package com.an.timeleft.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.timeleft.data.LeftUiModel
import com.an.timeleft.data.alpha
import com.an.timeleft.data.asString
import com.an.timeleft.ui.viewmodel.TimeLeftViewModel

@Composable
fun MainApp(
    viewModel: TimeLeftViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.currentUiState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    ).value

    val showDatePicker = viewModel.showDatePicker.collectAsStateWithLifecycle().value

    Column (
        modifier = modifier.fillMaxSize().background(
            color = MaterialTheme.colorScheme.primary
        ).padding(10.dp).fillMaxSize()
    ) {
        // Grid layout
        DotGridScreen(
            totalDots = uiState.totalTime.toInt(),
            progress = uiState.timeCompleted.toInt()
        )

        // Bottom layout with title & days/percentage left.
        BottomLayout(
            uiState = uiState,
            onTitleClicked = { viewModel.onTitleClicked() },
            onTimeLeftClicked = { viewModel.onTimeLeftClicked() }
        )
    }

    if (showDatePicker) {
        DatePickerModal(
            initialDate = viewModel.getBirthDate(),
            onDateSelected = { viewModel.onBirthDateSelected(it) },
            onDismiss = { viewModel.onDatePickerDismissed() }
        )
    }
}

@Composable
fun BottomLayout(
    uiState: LeftUiModel,
    onTitleClicked: () -> Unit,
    onTimeLeftClicked: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            TextButton(
                onClick = onTitleClicked
            ) {
                Text(
                    text = uiState.title,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = FontFamily.Monospace
                    ),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1f))

            TextButton(
                onClick = onTimeLeftClicked
            ) {
                Text(
                    text = uiState.timeLeftString.asString(),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary.copy(uiState.timeLeftString.alpha()),
                        fontFamily = FontFamily.Monospace
                    ),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

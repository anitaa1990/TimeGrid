package com.an.timeleft.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.an.timeleft.R
import com.an.timeleft.data.LeftUiModel
import com.an.timeleft.ui.viewmodel.TimeLeftViewModel

@Composable
fun MainApp(
    viewModel: TimeLeftViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.currentUiState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    ).value

    val timeLeftString = if (uiState.isTimeLeftInDays) {
        stringResource(R.string.time_left_in_days, uiState.timeLeft)
    } else stringResource(R.string.time_left_in_percent, uiState.timeLeftInPercentage)

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
            timeLeft = timeLeftString,
            onTitleClicked = { viewModel.onTitleClicked() },
            onTimeLeftClicked = { viewModel.onTimeLeftClicked() }
        )
    }
}

@Composable
fun BottomLayout(
    uiState: LeftUiModel,
    timeLeft: String,
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
                    text = timeLeft,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = FontFamily.Monospace
                    ),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun DotGridScreen(
    totalDots: Int,
    progress: Int
) {
    // Determine the dynamic dot size based on the total number of dots
    val maxDotSize = 50.dp // Maximum size when dots are few
    val minDotSize = 20.dp  // Minimum size when dots are many
    val dynamicDotSize = remember(totalDots) {
        when {
            totalDots <= 50 -> maxDotSize
            totalDots in 51..200 -> 15.dp
            else -> minDotSize
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(dynamicDotSize),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(totalDots) { index ->
            // Pass whether the dot should be white (progress completed) or gray (remaining)
            val color = if (index < progress) {
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
            } else MaterialTheme.colorScheme.onPrimary
            DrawDot(color = color, dotSize = dynamicDotSize)
        }
    }
}

@Composable
private fun DrawDot(
    color: Color,
    dotSize: Dp
) {
    Canvas(
        modifier = Modifier
            .size(dotSize) // Customize the dot size
            .padding(5.dp) // Space between dots
    ) {
        drawCircle(
            color = color,
            radius = size.minDimension / 2, // Half the size to get a circular dot
            center = center // Ensures the circle is drawn at the center of the canvas
        )
    }
}
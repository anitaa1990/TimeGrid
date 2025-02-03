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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.an.timeleft.ui.viewmodel.TimeLeftViewModel
import com.an.timeleft.util.TimeLeftUtil

@Composable
fun MainApp(
    viewModel: TimeLeftViewModel,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier.fillMaxSize().background(
            color = MaterialTheme.colorScheme.primary
        ).padding(10.dp).fillMaxSize()
    ) {
        // Grid layout
        DotGridScreen(
            totalDots = TimeLeftUtil.getTotalDaysInYear().toInt(),
            progress = TimeLeftUtil.getDaysCompletedInYear().toInt()
        )

        // Bottom layout with title & days/percentage left.
        BottomLayout(
            title = "2025",
            timeLeft = "365 days left"
        )
    }
}

@Composable
fun BottomLayout(
    title: String,
    timeLeft: String
) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row {
            Text(
                text = title,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = FontFamily.Monospace
                ),
                fontSize = 18.sp
            )

            Spacer(Modifier.weight(1f))

            Text(
                text = timeLeft,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = FontFamily.Monospace
                ),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun DotGridScreen(
    totalDots: Int,
    progress: Int
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp), // Add vertical spacing
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(totalDots) { index ->
            // Pass whether the dot should be white (progress completed) or gray (remaining)
            val color = if (index < progress) {
                MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f)
            } else MaterialTheme.colorScheme.onPrimary
            DrawDot(color = color)
        }
    }
}

@Composable
private fun DrawDot(
    color: Color
) {
    Canvas(
        modifier = Modifier
            .size(20.dp) // Customize the dot size
            .padding(5.dp) // Space between dots
    ) {
        drawCircle(
            color = color,
            radius = size.minDimension / 2, // Half the size to get a circular dot
            center = center // Ensures the circle is drawn at the center of the canvas
        )
    }
}
package com.an.timeleft.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TimeGridScreen(
    totalDots: Int,
    progress: Int
) {
    // Determine the dynamic dot size based on the total number of dots
    val maxDotSize = 50.dp // Maximum size when dots are few
    val minDotSize = 11.5.dp  // Minimum size when dots are many
    val dynamicDotSize = remember(totalDots) {
        when {
            totalDots <= 50 -> maxDotSize
            totalDots in 51..500 -> 20.dp
            else -> minDotSize
        }
    }

    val dynamicPadding = remember(totalDots) {
        when {
            totalDots <= 50 -> 5.dp
            totalDots in 51..500 -> 5.dp
            else -> 2.dp
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
            DrawDot(color = color, dotSize = dynamicDotSize, padding = dynamicPadding)
        }
    }
}

@Composable
private fun DrawDot(
    color: Color,
    dotSize: Dp,
    padding: Dp
) {
    Canvas(
        modifier = Modifier
            .size(dotSize) // Customize the dot size
            .padding(padding) // Space between dots
    ) {
        drawCircle(
            color = color,
            radius = size.minDimension / 2, // Half the size to get a circular dot
            center = center // Ensures the circle is drawn at the center of the canvas
        )
    }
}

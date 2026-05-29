package com.hellogithub.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.hellogithub.app.data.remote.dto.StarHistoryDto

@Composable
fun StarChart(
    starHistory: StarHistoryDto,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        if (starHistory.y.isEmpty()) return@Canvas

        val maxY = starHistory.y.max().toFloat()
        val minY = starHistory.y.min().toFloat()
        val range = (maxY - minY).coerceAtLeast(1f)
        val stepX = size.width / (starHistory.y.size - 1).coerceAtLeast(1)

        val path = Path()
        starHistory.y.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minY) / range) * size.height * 0.8f - size.height * 0.1f
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
        )

        starHistory.y.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minY) / range) * size.height * 0.8f - size.height * 0.1f
            drawCircle(
                color = primaryColor,
                radius = 3.dp.toPx(),
                center = Offset(x, y),
            )
        }
    }
}

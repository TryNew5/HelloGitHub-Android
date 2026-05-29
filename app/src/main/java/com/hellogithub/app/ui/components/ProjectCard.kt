package com.hellogithub.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hellogithub.app.data.remote.dto.HomeItemDto

@Composable
fun ProjectCard(
    item: HomeItemDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Author avatar
            AsyncImage(
                model = item.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title — bold, prominent
                Text(
                    text = item.title.ifEmpty { item.titleEn ?: "" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Summary
                val summary = item.summary.ifEmpty { item.summaryEn ?: "" }
                if (summary.isNotEmpty()) {
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Bottom row: author + language + stats
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (item.primaryLang.isNotEmpty()) {
                        LanguageBadge(
                            language = item.primaryLang,
                            colorHex = item.langColor,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (item.stars > 0) {
                        Text(
                            text = "⭐ ${formatStarCount(item.stars)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else if (item.starsStr.isNotEmpty()) {
                        Text(
                            text = "⭐ ${item.starsStr}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private fun formatStarCount(stars: Int): String {
    return when {
        stars >= 1000 -> "${stars / 1000}.${(stars % 1000) / 100}k"
        else -> stars.toString()
    }
}

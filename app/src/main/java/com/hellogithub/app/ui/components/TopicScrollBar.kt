package com.hellogithub.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hellogithub.app.data.remote.dto.TagDto

@Composable
fun TopicScrollBar(
    topics: List<TagDto>,
    selectedTopicId: String,
    onTopicSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selectedTopicId == "all",
            onClick = { onTopicSelected("all") },
            label = { Text("All") },
        )
        topics.forEach { tag ->
            FilterChip(
                selected = selectedTopicId == tag.tid,
                onClick = { onTopicSelected(tag.tid) },
                label = { Text(tag.nameEn ?: tag.name) },
            )
        }
    }
}

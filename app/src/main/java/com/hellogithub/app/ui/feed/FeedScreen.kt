package com.hellogithub.app.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hellogithub.app.ui.components.ProjectCard
import com.hellogithub.app.ui.components.SkeletonProjectCard
import com.hellogithub.app.ui.components.TopicScrollBar
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: FeedViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Topic scroll bar
        TopicScrollBar(
            topics = uiState.tags,
            selectedTopicId = uiState.selectedTopicId,
            onTopicSelected = { viewModel.selectTopic(it) },
        )

        // Sort + Rank filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = uiState.selectedSort == "featured",
                onClick = { viewModel.selectSort("featured") },
                label = { Text("Featured") },
            )
            FilterChip(
                selected = uiState.selectedSort == "all",
                onClick = { viewModel.selectSort("all") },
                label = { Text("All") },
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = uiState.selectedRank == null,
                onClick = { viewModel.selectRank(null) },
                label = { Text("Latest") },
            )
            FilterChip(
                selected = uiState.selectedRank == "monthly",
                onClick = { viewModel.selectRank("monthly") },
                label = { Text("Monthly") },
            )
            FilterChip(
                selected = uiState.selectedRank == "yearly",
                onClick = { viewModel.selectRank("yearly") },
                label = { Text("Yearly") },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Content
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                uiState.isLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(6) {
                            SkeletonProjectCard()
                        }
                    }
                }
                uiState.error != null && uiState.items.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadFeed() }) {
                            Text("重试")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.items) { item ->
                            ProjectCard(
                                item = item,
                                onClick = { onNavigateToDetail(item.itemId) },
                            )
                        }
                    }
                }
            }
        }
    }
}

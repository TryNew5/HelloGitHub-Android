package com.hellogithub.app.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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

        // Sort + Rank chips — merged into one compact row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FilterChip(
                selected = uiState.selectedSort == "featured",
                onClick = { viewModel.selectSort("featured") },
                label = { Text("精选") },
                modifier = Modifier.height(28.dp),
            )
            FilterChip(
                selected = uiState.selectedSort == "all",
                onClick = { viewModel.selectSort("all") },
                label = { Text("全部") },
                modifier = Modifier.height(28.dp),
            )
            FilterChip(
                selected = uiState.selectedRank == null,
                onClick = { viewModel.selectRank(null) },
                label = { Text("最新") },
                modifier = Modifier.height(28.dp),
            )
            FilterChip(
                selected = uiState.selectedRank == "monthly",
                onClick = { viewModel.selectRank("monthly") },
                label = { Text("本月") },
                modifier = Modifier.height(28.dp),
            )
            FilterChip(
                selected = uiState.selectedRank == "yearly",
                onClick = { viewModel.selectRank("yearly") },
                label = { Text("年度") },
                modifier = Modifier.height(28.dp),
            )
        }

        // Thin divider instead of empty space
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp)

        // Content — fills remaining space
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                uiState.isLoading -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(6) { SkeletonProjectCard() }
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
                        OutlinedButton(onClick = { viewModel.loadFeed() }) {
                            Text("重试")
                        }
                    }
                }
                else -> {
                    val listState = rememberLazyListState()

                    val shouldLoadMore = remember {
                        derivedStateOf {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                            val totalItems = listState.layoutInfo.totalItemsCount
                            totalItems > 0 && lastVisible >= totalItems - 3
                        }
                    }

                    LaunchedEffect(shouldLoadMore.value) {
                        if (shouldLoadMore.value && uiState.hasMore && !uiState.isLoadingMore) {
                            viewModel.loadMore()
                        }
                    }

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            items = uiState.items,
                            key = { it.rid.ifEmpty { it.itemId }.ifEmpty { it.fullName } },
                        ) { item ->
                            ProjectCard(
                                item = item,
                                onClick = { onNavigateToDetail(item.rid.ifEmpty { item.itemId }) },
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item(key = "load_more") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }

                        if (!uiState.hasMore && uiState.items.isNotEmpty()) {
                            item(key = "end_hint") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "— 已经到底啦 —",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

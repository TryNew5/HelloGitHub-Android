package com.hellogithub.app.ui.periodical

import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hellogithub.app.data.remote.dto.PeriodicalCategoryDto
import com.hellogithub.app.data.remote.dto.PeriodicalItemDto
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PeriodicalScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: PeriodicalViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is PeriodicalUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is PeriodicalUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* retry via init */ }) { Text("重试") }
                }
            }
        }
        is PeriodicalUiState.VolumeList -> {
            PeriodicalContent(
                issues = state.issues,
                selectedIssueNum = state.selectedIssueNum,
                repoTotal = state.repoTotal,
                publishAt = "",
                categories = emptyList(),
                isLoadingDetail = true,
                onSelectIssue = { viewModel.selectIssue(it) },
                onNavigateToDetail = onNavigateToDetail,
            )
        }
        is PeriodicalUiState.VolumeDetail -> {
            PeriodicalContent(
                issues = state.issues,
                selectedIssueNum = state.selectedIssueNum,
                repoTotal = state.repoTotal,
                publishAt = state.publishAt,
                categories = state.categories,
                isLoadingDetail = false,
                onSelectIssue = { viewModel.selectIssue(it) },
                onNavigateToDetail = onNavigateToDetail,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PeriodicalContent(
    issues: List<com.hellogithub.app.data.remote.dto.PeriodicalIssueDto>,
    selectedIssueNum: Int?,
    repoTotal: Int,
    publishAt: String,
    categories: List<PeriodicalCategoryDto>,
    isLoadingDetail: Boolean,
    onSelectIssue: (Int) -> Unit,
    onNavigateToDetail: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Stats
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Text(
                text = "共收录 $repoTotal 个项目 · ${issues.size} 期月刊",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        // Issue picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            issues.forEach { issue ->
                FilterChip(
                    selected = selectedIssueNum == issue.num,
                    onClick = { onSelectIssue(issue.num) },
                    label = { Text("第 ${issue.num} 期", maxLines = 1) },
                )
            }
        }

        HorizontalDivider()

        if (isLoadingDetail) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (categories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("暂无内容", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            // Category sections with sticky headers
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Publish date header
                if (publishAt.isNotEmpty()) {
                    item {
                        Text(
                            text = publishAt.take(10),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }

                categories.forEach { category ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            Text(
                                text = category.categoryName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                    items(
                        items = category.items,
                        key = { it.rid },
                    ) { item ->
                        PeriodicalProjectCard(
                            item = item,
                            onClick = { onNavigateToDetail(item.rid) },
                        )
                    }
                }

                // Bottom spacer
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun PeriodicalProjectCard(
    item: PeriodicalItemDto,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Project image or placeholder
            if (!item.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                // Project name
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Full name
                Text(
                    text = item.fullName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stats
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "⭐ ${formatStarCount(item.stars)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${item.forks} forks",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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

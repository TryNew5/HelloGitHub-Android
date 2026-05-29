package com.hellogithub.app.ui.rank

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hellogithub.app.data.remote.dto.HomeItemDto
import com.hellogithub.app.ui.components.LanguageBadge
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: RankViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Filled.Whatshot,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "热门项目排行榜",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        // Period selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("weekly" to "本周", "monthly" to "本月", "yearly" to "年度").forEach { (key, label) ->
                FilterChip(
                    selected = uiState.selectedPeriod == key,
                    onClick = { viewModel.selectPeriod(key) },
                    label = { Text(label) },
                )
            }
        }

        // Sub-filter: months for monthly, years for yearly
        when (uiState.selectedPeriod) {
            "monthly" -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    viewModel.getRecentMonths().forEach { ym ->
                        FilterChip(
                            selected = uiState.selectedYearMonth == ym,
                            onClick = { viewModel.selectYearMonth(ym) },
                            label = { Text(ym.label) },
                        )
                    }
                }
            }
            "yearly" -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    viewModel.getAvailableYears().forEach { year ->
                        FilterChip(
                            selected = uiState.selectedYear == year,
                            onClick = { viewModel.selectYear(year) },
                            label = { Text("${year}年") },
                        )
                    }
                }
            }
            // weekly: no sub-filter
        }

        HorizontalDivider()

        // Content
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null && uiState.items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = uiState.error ?: "", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadRanking() }) { Text("重试") }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        itemsIndexed(
                            items = uiState.items,
                            key = { _, item -> item.rid.ifEmpty { item.itemId }.ifEmpty { item.fullName } },
                        ) { index, item ->
                            RankCard(
                                rank = index + 1,
                                item = item,
                                onClick = { onNavigateToDetail(item.rid.ifEmpty { item.itemId }) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankCard(
    rank: Int,
    item: HomeItemDto,
    onClick: () -> Unit,
) {
    val rankColor = when (rank) {
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.tertiary
        3 -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(rankColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = rankColor,
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Avatar
            AsyncImage(
                model = item.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title.ifEmpty { item.titleEn ?: "" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.author,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (item.primaryLang.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        LanguageBadge(language = item.primaryLang, colorHex = item.langColor)
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Popularity (clicks, since ranking API doesn't return stars)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatCount(item.clicksTotal)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = rankColor,
                )
                Text(
                    text = "浏览",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 10000 -> "${count / 1000}k"
        count >= 1000 -> "${count / 1000}.${(count % 1000) / 100}k"
        else -> count.toString()
    }
}

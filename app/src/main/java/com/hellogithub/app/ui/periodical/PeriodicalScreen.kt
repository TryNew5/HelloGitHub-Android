package com.hellogithub.app.ui.periodical

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hellogithub.app.ui.components.ProjectCard
import org.koin.androidx.compose.koinViewModel

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
                    Button(onClick = { /* viewModel retries via init */ }) { Text("重试") }
                }
            }
        }
        is PeriodicalUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                // Issue picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.issues.forEach { issue ->
                        val selected = state.selectedIssue?.volumeId == issue.volumeId
                        FilterChip(
                            selected = selected,
                            onClick = { viewModel.selectIssue(issue.volumeId) },
                            label = {
                                Text(
                                    text = issue.nameEn ?: issue.name,
                                    maxLines = 1,
                                )
                            },
                        )
                    }
                }

                HorizontalDivider()

                // Category sections with sticky headers
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    state.categories.forEach { category ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.background,
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                            }
                        }
                        items(category.items) { item ->
                            ProjectCard(
                                item = item,
                                onClick = { onNavigateToDetail(item.itemId) },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

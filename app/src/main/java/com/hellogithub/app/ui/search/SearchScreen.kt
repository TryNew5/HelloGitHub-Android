package com.hellogithub.app.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hellogithub.app.data.remote.dto.HomeItemDto
import com.hellogithub.app.ui.components.ProjectCard
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onQueryChanged(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("搜索开源项目...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.large,
        )

        // Snapshot read ONCE — prevents ClassCastException when state changes
        // mid-composition (e.g. coroutine sets Searching while we're composing Success).
        val state = uiState
        when (state) {
            is SearchUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "输入关键词搜索感兴趣的开源项目",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            is SearchUiState.Searching -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            is SearchUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "未找到相关项目",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            is SearchUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            is SearchUiState.Success -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        items = state.results,
                        key = { it.rid.ifEmpty { it.itemId }.ifEmpty { it.fullName } },
                    ) { item ->
                        ProjectCard(
                            item = item,
                            onClick = { onNavigateToDetail(item.rid.ifEmpty { item.itemId }) },
                        )
                    }
                }
            }
        }
    }
}

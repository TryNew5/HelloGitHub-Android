package com.hellogithub.app.ui.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hellogithub.app.data.remote.dto.RepositoryDto
import com.hellogithub.app.ui.components.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    rid: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(rid) {
        viewModel.loadDetail(rid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = (uiState as? DetailUiState.Success)
                            ?.repo?.title?.takeIf { it.isNotEmpty() } ?: "项目详情",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    val repo = (uiState as? DetailUiState.Success)?.repo
                    if (repo != null) {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.url))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Filled.OpenInBrowser, contentDescription = "浏览器打开")
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, repo.title)
                                putExtra(Intent.EXTRA_TEXT, "${repo.title} — ${repo.description}\n${repo.url}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "分享"))
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "分享")
                        }
                    }
                },
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                ) {
                    Column {
                        SkeletonProjectCard()
                        Spacer(modifier = Modifier.height(16.dp))
                        SkeletonProjectCard()
                    }
                }
            }
            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadDetail(rid) }) {
                            Text("重试")
                        }
                    }
                }
            }
            is DetailUiState.Success -> {
                DetailContent(
                    repo = state.repo,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    repo: RepositoryDto,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = repo.titleEn ?: repo.title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Author + Stars
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = repo.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = repo.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "⭐ ${repo.starsStr}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Star history chart
        if (repo.starHistory != null && repo.starHistory.y.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Star History",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StarChart(starHistory = repo.starHistory)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Meta info chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (repo.primaryLang.isNotEmpty()) {
                AssistChip(onClick = {}, label = { Text(repo.primaryLang) })
            }
            if (repo.hasChinese) {
                AssistChip(onClick = {}, label = { Text("支持中文") })
            }
            if (repo.isActive) {
                AssistChip(onClick = {}, label = { Text("活跃") })
            }
            if (repo.license.isNotEmpty()) {
                AssistChip(onClick = {}, label = { Text(repo.license) })
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tags
        if (repo.tags.isNotEmpty()) {
            TagRow(tags = repo.tags.map { it.nameEn ?: it.name })
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Quick Links
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repo.homepage?.let { url ->
                TextButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }) { Text("官网") }
            }
            repo.document?.let { url ->
                TextButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }) { Text("文档") }
            }
            repo.download?.let { url ->
                TextButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }) { Text("下载") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "项目介绍",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(8.dp))

        val markdownContent = repo.summaryEn ?: repo.summary.ifEmpty { repo.description }
        MarkdownView(
            markdown = markdownContent,
            modifier = Modifier.heightIn(min = 100.dp),
        )

        // Screenshots
        if (!repo.imageUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "截图预览", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(
                model = repo.imageUrl,
                contentDescription = "项目截图",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillWidth,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            StatItem("${repo.stars}", "Stars")
            StatItem("${repo.forks}", "Forks")
            StatItem("${repo.votes}", "Votes")
            StatItem("%.1f".format(repo.score), "Score")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

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
import androidx.compose.ui.text.font.FontWeight
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
                    val repo = (uiState as? DetailUiState.Success)?.repo
                    Text(
                        text = repo?.let { r ->
                            r.title.ifEmpty { r.titleEn ?: r.name.ifEmpty { r.fullName } }
                        } ?: "项目详情",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                actions = {
                    val repo = (uiState as? DetailUiState.Success)?.repo
                    if (repo != null) {
                        IconButton(onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repo.url)))
                        }) {
                            Icon(
                                Icons.Filled.OpenInBrowser,
                                contentDescription = "浏览器打开",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                        IconButton(onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, repo.title)
                                putExtra(Intent.EXTRA_TEXT, "${repo.title} — ${repo.summary.ifEmpty { repo.description }}\n${repo.url}")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "分享"))
                        }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "分享",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(12.dp),
                ) {
                    Column {
                        SkeletonProjectCard()
                        Spacer(modifier = Modifier.height(12.dp))
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
                        OutlinedButton(onClick = { viewModel.loadDetail(rid) }) {
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
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = repo.title.ifEmpty { repo.titleEn ?: repo.name.ifEmpty { repo.fullName } },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Full name
        if (repo.fullName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = repo.fullName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Author + Stars
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = repo.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = repo.author,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "⭐ ${repo.starsStr}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Meta chips
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (repo.primaryLang.isNotEmpty()) {
                SuggestionChip(onClick = {}, label = { Text(repo.primaryLang) })
            }
            if (repo.hasChinese) {
                SuggestionChip(onClick = {}, label = { Text("中文") })
            }
            if (repo.isActive) {
                SuggestionChip(onClick = {}, label = { Text("活跃") })
            }
            if (repo.license.isNotEmpty()) {
                SuggestionChip(onClick = {}, label = { Text(repo.license) })
            }
        }

        // Tags
        if (repo.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            TagRow(tags = repo.tags.map { it.nameEn ?: it.name })
        }

        // Links
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repo.homepage?.let { url ->
                TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) {
                    Text("官网", style = MaterialTheme.typography.labelMedium)
                }
            }
            repo.document?.let { url ->
                TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) {
                    Text("文档", style = MaterialTheme.typography.labelMedium)
                }
            }
            repo.download?.let { url ->
                TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }) {
                    Text("下载", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Star history chart
        if (repo.starHistory != null && repo.starHistory.y.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Star History",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    StarChart(starHistory = repo.starHistory)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Description
        Text(
            text = "项目介绍",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(6.dp))

        val markdownContent = repo.summary.ifEmpty { repo.summaryEn ?: "" }
        MarkdownView(
            markdown = markdownContent,
            modifier = Modifier.heightIn(min = 60.dp),
        )

        // Screenshots
        if (!repo.imageUrl.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "截图预览",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            AsyncImage(
                model = repo.imageUrl,
                contentDescription = "项目截图",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillWidth,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

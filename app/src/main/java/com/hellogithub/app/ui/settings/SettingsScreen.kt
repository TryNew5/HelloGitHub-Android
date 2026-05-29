package com.hellogithub.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ThemeViewModel = koinViewModel(),
) {
    val themeMode by viewModel.themeMode.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("设置") })

        Text(
            text = "主题",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        ThemeOption("跟随系统", ThemeMode.SYSTEM, themeMode, viewModel::setTheme)
        ThemeOption("始终亮色", ThemeMode.LIGHT, themeMode, viewModel::setTheme)
        ThemeOption("始终暗色", ThemeMode.DARK, themeMode, viewModel::setTheme)

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "关于",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        ListItem(
            headlineContent = { Text("HelloGitHub Android") },
            supportingContent = { Text("版本 1.0.0") },
        )
        ListItem(
            headlineContent = { Text("HelloGitHub 官网") },
            supportingContent = { Text("hellogithub.com") },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.Launch, null) },
        )
        ListItem(
            headlineContent = { Text("基于 HelloGitHub 开源社区") },
            supportingContent = {
                Text("发现和分享有趣、入门级的开源项目\n让对开源感兴趣的人在这里找到热爱")
            },
        )
    }
}

@Composable
private fun ThemeOption(
    label: String,
    mode: ThemeMode,
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(mode) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = current == mode, onClick = { onSelect(mode) })
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

package com.hellogithub.app.ui.components

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MarkdownView(
    markdown: String,
    modifier: Modifier = Modifier,
) {
    val isDark = luminance(MaterialTheme.colorScheme.background) < 0.5f

    val htmlContent = remember(markdown, isDark) {
        val escaped = markdown
            .replace("\\", "\\\\")
            .replace("`", "\\`")
            .replace("$", "\\$")
        val bg = if (isDark) "#0f172a" else "#ffffff"
        val text = if (isDark) "#f1f5f9" else "#0f172a"
        val code = if (isDark) "#334155" else "#f1f5f9"
        """
        <!DOCTYPE html>
        <html>
        <head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
        body{font-family:-apple-system,sans-serif;padding:8px 0;color:$text;background:$bg;line-height:1.6;font-size:14px;}
        pre{background:$code;padding:12px;border-radius:8px;overflow-x:auto;font-size:13px;}
        code{font-family:monospace;background:$code;padding:2px 4px;border-radius:4px;}
        img{max-width:100%;border-radius:8px;}
        a{color:#3b82f6;}
        h2,h3{margin-top:16px;}
        </style>
        </head>
        <body>$escaped</body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = false
                isVerticalScrollBarEnabled = false
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier.fillMaxWidth(),
    )
}

private fun luminance(color: Color): Float {
    return 0.299f * color.red + 0.587f * color.green + 0.114f * color.blue
}

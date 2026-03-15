package com.noxis.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen() {
    var url by remember { mutableStateOf("https://www.google.com") }
    var inputUrl by remember { mutableStateOf("https://www.google.com") }
    var webView by remember { mutableStateOf<WebView?>(null) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = { webView?.goBack() }) { Icon(Icons.Default.ArrowBack, null) }
            IconButton(onClick = { webView?.goForward() }) { Icon(Icons.Default.ArrowForward, null) }
            IconButton(onClick = { webView?.reload() }) { Icon(Icons.Default.Refresh, null) }
            OutlinedTextField(
                value = inputUrl, onValueChange = { inputUrl = it },
                modifier = Modifier.weight(1f).height(46.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    url = if (inputUrl.startsWith("http")) inputUrl else "https://$inputUrl"
                    webView?.loadUrl(url)
                })
            )
            IconButton(onClick = {
                url = if (inputUrl.startsWith("http")) inputUrl else "https://$inputUrl"
                webView?.loadUrl(url)
            }) { Icon(Icons.Default.Search, null) }
        }
        HorizontalDivider()
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    loadUrl(url)
                    webView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

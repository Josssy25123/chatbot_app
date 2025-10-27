@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.chatbotapp.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import android.webkit.JavascriptInterface

// JavaScript Interface class to pass data from WebView to Kotlin
class HtmlCaptureInterface(private val onHtmlReady: (String) -> Unit) {
    @JavascriptInterface
    @Suppress("unused")
    fun processHTML(html: String) {
        Log.d("WebView", "HTML received from WebView. Length: ${html.length}")
        // Post the result back to the Compose thread via the callback
        onHtmlReady(html)
    }
}

@Composable
fun WebViewScreen(
    url: String,
    onClose: () -> Unit,
    onHtmlScraped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    val context = LocalContext.current

    // Create the interface instance once
    val htmlCaptureInterface = remember { HtmlCaptureInterface(onHtmlScraped) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Degreeworks Gateway") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Browser")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box( // Use Box to layer the WebView and the FAB
            modifier = Modifier
                .padding(paddingValues) // Apply the outer padding here
                .fillMaxSize()
        ) {
            // AndroidView is used to embed traditional Android Views (like WebView) into Compose
            AndroidView(
                modifier = Modifier.fillMaxSize(), // Correct modifier: fill the Box
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewRef = this // Capture the reference

                        // Enable JavaScript for the login portal to function
                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true

                        // Add the JavaScript interface
                        addJavascriptInterface(htmlCaptureInterface, "HtmlCapturer")

                        // Set WebViewClient to keep navigation *inside* the WebView
                        webViewClient = WebViewClient()
                        loadUrl(url)
                    }
                },
                update = { webView ->
                    // Ensures the WebView loads the URL if the Composable is reused
                    if (webView.url != url) {
                        webView.loadUrl(url)
                    }
                }
            )

            // FAB to trigger the scraping action
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    val webView = webViewRef
                    if (webView != null) {
                        // JavaScript to get the full page HTML (Note: this must be one line)
                        // This calls the processHTML method in the HtmlCapturer interface
                        val jsCode = "javascript:HtmlCapturer.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"

                        // Execute the JavaScript
                        webView.evaluateJavascript(jsCode, null)

                        // Give the user feedback while waiting for the network response
                        // (You should add a progress indicator here later)
                        Log.d("WebView", "Scrape initiated. Waiting for HTML capture...")
                    }
                },
                icon = { Icon(Icons.Filled.CloudUpload, contentDescription = "Scrape") },
                text = { Text("Scrape Degreeworks") },
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

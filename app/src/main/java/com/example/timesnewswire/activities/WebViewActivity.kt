package com.example.timesnewswire.activities

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.timesnewswire.R

class WebViewActivity: AppCompatActivity() {
    companion object {
        val URL_EXTRA: String by lazy { "URL_EXTRA" }
    }

    private val defaultURL: String by lazy { this.resources.getString(R.string.webView_Default_URL) }
    private val webView: WebView by lazy { findViewById<WebView>(R.id.webView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView.webViewClient = WebViewClient()
        val intent: Intent = intent
        val urlToLoad: String = if (intent.hasExtra(URL_EXTRA))
            intent.getStringExtra(URL_EXTRA)
        else
            defaultURL

        webView.loadUrl(urlToLoad)
    }

    override fun onBackPressed() {
        if (webView.canGoBack())
            webView.goBack()
        else
            super.onBackPressed()
    }
}
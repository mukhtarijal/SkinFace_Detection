package com.dicoding.capstone.dermaface.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.databinding.ActivityArticleBinding

class ArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val articleUrl = intent.getStringExtra(EXTRA_ARTICLE_URL)
        if (!articleUrl.isNullOrEmpty()) {
            setupWebView(articleUrl)
        } else {
            Toast.makeText(this, R.string.invalid_article_url, Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url: String) {
        binding.webView.apply {
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.progressBar.visibility = View.VISIBLE
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.progressBar.visibility = View.GONE
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                }

                @Deprecated("Deprecated in Java", ReplaceWith("false"))
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    binding.progressBar.visibility = View.GONE

                    if (request?.isForMainFrame == true) {
                        Toast.makeText(this@ArticleActivity, R.string.load_error, Toast.LENGTH_SHORT).show()
                        binding.webView.loadUrl("about:blank")
                    }
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    binding.progressBar.visibility = View.GONE

                    if (request?.isForMainFrame == true) {
                        Toast.makeText(this@ArticleActivity, R.string.load_error, Toast.LENGTH_SHORT).show()
                        binding.webView.loadUrl("about:blank")
                    }
                }
            }
            settings.javaScriptEnabled = true
            loadUrl(url)
        }
    }

    companion object {
        const val EXTRA_ARTICLE_URL = "com.example.dermaface.EXTRA_ARTICLE_URL"
    }
}

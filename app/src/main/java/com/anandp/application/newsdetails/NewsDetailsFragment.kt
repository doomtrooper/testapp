package com.anandp.application.newsdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anandp.application.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_news_details.*
import android.webkit.WebViewClient



class NewsDetailsFragment: DaggerFragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.webViewClient = WebViewClient()
        webView.loadUrl(arguments?.get("url") as String? ?: "www.google.com")
    }
}

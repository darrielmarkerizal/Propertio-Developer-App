package com.propertio.developer.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.propertio.developer.databinding.FragmentVideoTutorialBinding
import java.util.regex.Pattern

class TutorialYoutubeDialog : DialogFragment() {
    private val binding by lazy {
        FragmentVideoTutorialBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadVideo()
    }


    private fun loadVideo() {
        val webView: WebView = binding.webViewVideoTutor
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://www.youtube.com/embed/u9BpBBl-Hyg")


    }


}
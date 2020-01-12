package moe.baihumaru.android.ui.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.WebviewFragmentBinding
import moe.baihumaru.android.ui.defaults.CoreNestedFragment
import javax.inject.Inject

class WebViewFragment : CoreNestedFragment<WebviewFragmentBinding>() {

  companion object {
    const val TAG = "web_view"
    private const val ARG_URL = "arg_url"
    @JvmStatic
    fun intentFactory(url: String) = WebViewFragment().apply {
      arguments = bundleOf(ARG_URL to url)
    }
  }

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): WebviewFragmentBinding {
    return WebviewFragmentBinding.inflate(inflater, container, false)
  }

  @Inject
  lateinit var client: CompatWebViewClient

  @SuppressLint("SetJavaScriptEnabled")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding.webView) {
      webViewClient = client
      with(settings) {
        javaScriptEnabled = true
      }
      loadUrl(requireNotNull(arguments?.getString(ARG_URL)))
    }
  }

  override fun contextualTitle(): String {
    return getString(R.string.app_name)
  }

  class CompatWebViewClient @Inject constructor() : WebViewClient()
}
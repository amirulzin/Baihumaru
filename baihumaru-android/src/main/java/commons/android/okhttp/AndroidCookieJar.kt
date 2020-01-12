package commons.android.okhttp

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import javax.inject.Inject

class AndroidCookieJar @Inject constructor() : CookieJar {

  private val cookieManager by lazy {
    CookieManager.getInstance()
  }

  override fun loadForRequest(url: HttpUrl): List<Cookie> {
    return cookieManager.getCookie(url.toString())
      ?.takeIf(String::isNotEmpty)
      ?.let { cookies ->
        cookies.split(";").mapNotNull { Cookie.parse(url, it) }
      } ?: emptyList()
  }

  override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
    val urlString = url.toString()
    for (cookie in cookies) {
      cookieManager.setCookie(urlString, cookie.toString())
    }
  }
}

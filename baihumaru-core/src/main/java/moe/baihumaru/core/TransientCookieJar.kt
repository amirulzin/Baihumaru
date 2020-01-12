package moe.baihumaru.core

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class TransientCookieJar : CookieJar {
  private val map = mutableMapOf<String, List<Cookie>>()

  override fun loadForRequest(url: HttpUrl): List<Cookie> {
    return url.topPrivateDomain()?.let { map.getOrElse(it, ::emptyList) } ?: emptyList()
  }

  override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
    url.topPrivateDomain()?.let { map[it] = cookies }
  }
}
package moe.baihumaru.core

import okhttp3.Interceptor
import okhttp3.Response

class AgentInterceptor : Interceptor {
  companion object {
    const val USER_AGENT_KEY = "User-Agent"
    const val USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.117 Safari/537.36"
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.request().newBuilder()
      .addHeader(USER_AGENT_KEY, USER_AGENT_VALUE)
      .build()
      .let(chain::proceed)
  }
}
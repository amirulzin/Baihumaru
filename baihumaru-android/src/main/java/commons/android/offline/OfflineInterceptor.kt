package commons.android.offline

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

class OfflineInterceptor(private val controller: OfflineController) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    if (controller.shouldBeOffline()) {
      val request = chain.request()
        .newBuilder()
        .cacheControl(CacheControl.FORCE_CACHE)
        .build()
      val response = chain.proceed(request)
      //OkHttp code for Unsatisfiable Request for cache
      if (response.code != 504) {
        return response
      }
    }
    return chain.proceed(chain.request())
  }
}
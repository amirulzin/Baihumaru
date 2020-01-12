package moe.baihumaru.android

import android.content.Context
import com.squareup.moshi.Moshi
import commons.android.arch.RetrofitRxErrorHandler
import commons.android.dagger.ApplicationContext
import commons.android.dagger.ApplicationScope
import commons.android.okhttp.AndroidCookieJar
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.OkHttpClient

@Module
class NetworkModule {
  @Provides
  fun errorHandler(): RetrofitRxErrorHandler {
    return RetrofitRxErrorHandler()
  }

  @ApplicationScope
  @Provides
  fun moshi(): Moshi {
    return Moshi.Builder().build()
  }

  @ApplicationScope
  @Provides
  fun okHttp(cache: Cache, cookieJar: CookieJar): OkHttpClient {
    return OkHttpClient.Builder()
      .cookieJar(cookieJar)
      .cache(cache)
      .build()
  }

  @ApplicationScope
  @Provides
  fun cookieJar(androidCookieJar: AndroidCookieJar): CookieJar {
    return androidCookieJar
  }

  @ApplicationScope
  @Provides
  fun okHttpCache(@ApplicationContext context: Context): Cache {
    return Cache(context.cacheDir, 100 * 1024 * 1024)
  }
}
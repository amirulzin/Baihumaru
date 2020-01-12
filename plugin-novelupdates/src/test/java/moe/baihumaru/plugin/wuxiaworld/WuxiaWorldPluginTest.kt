package moe.baihumaru.plugin.wuxiaworld

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

class WuxiaWorldPluginTest {

  private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BASIC
    }).build()

  private val plugin = WuxiaWorldPlugin().apply {
    injectOkHttpClient(client)
  }

  @Test
  fun loadNovels() {
    plugin.provideNovels().apply {
      require(isNotEmpty())
    }
  }

  @Test
  fun loadChapters() {
    val novelId = plugin.provideNovels().last()
    plugin.provideChapters(novelId).apply {
      require(isNotEmpty())
    }
  }

  @Test
  fun loadChapter() {
    val novelId = plugin.provideNovels().last()
    val chapterId = plugin.provideChapters(novelId).last()
    plugin.provideChapter(novelId, chapterId)
  }
}
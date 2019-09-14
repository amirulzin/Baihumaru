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
    plugin.provideChapters(plugin.provideNovels().random()).apply {
      require(isNotEmpty())
    }
  }

  @Test
  fun loadChapter() {
    val novelId = plugin.provideNovels().random()
    val chapterId = plugin.provideChapters(novelId).random()
    plugin.provideChapter(novelId, chapterId)
  }
}
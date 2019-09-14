package moe.baihumaru.plugin.novelupdates

import moe.baihumaru.core.*
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*

abstract class NovelUpdatesPlugin : Plugin {
  private lateinit var client: OkHttpClient

  override fun injectOkHttpClient(client: OkHttpClient) {
    this.client = client
  }

  abstract val groupNameId: String

  abstract fun canHandle(url: HttpUrl): Boolean

  abstract fun parseChapter(document: Document, chapterUrl: HttpUrl): Chapter

  abstract fun parseChapterBehavior(document: Document, chapterUrl: HttpUrl, parsedChapter: Chapter): ChapterBehavior

  override fun provideChapter(novelId: DefaultNovelId, chapterId: DefaultChapterId): Pair<Chapter, ChapterBehavior> {
    val (document, url) = getAsDocumentWithUrl(retrieveChapterUrlBy(chapterId))
    if (canHandle(url)) {
      with(parseChapter(document, url)) {
        return this to parseChapterBehavior(document, url, this)
      }
    } else throw IOException("${javaClass.simpleName} can't handle $url")
  }

  override fun provideNovels(pageTraveler: PageTraveler): List<DefaultNovelId> {
    return parseNovels(getAsDocument("https://www.novelupdates.com/group/$groupNameId/"))
      .sortedBy(DefaultNovelId::title)
  }

  override fun provideChapters(novelId: DefaultNovelId, pageTraveler: PageTraveler): List<DefaultChapterId> {
    val url = "https://www.novelupdates.com/wp-admin/admin-ajax.php"
    val response = client.newCall(Request.Builder()
      .url(url)
      .post(FormBody.Builder()
        .add("action", "nd_getchapters")
        .add("mygrr", "0")
        .add("mypostid", retrievePostIdBy(novelId))
        .build())
      .build())
      .execute()
    return parseChapters(toDocument(response, url, isBodyFragment = true))
  }

  private fun getAsDocument(url: String): Document {
    val response = client.newCall(Request.Builder()
      .get()
      .url(url)
      .build()
    ).execute()
    return toDocument(response, url)
  }

  private fun getAsDocumentWithUrl(url: String): Pair<Document, HttpUrl> {
    val response = client.newCall(Request.Builder()
      .get()
      .url(url)
      .build()
    ).execute()
    return toDocument(response, url) to response.request.url
  }

  private fun toDocument(response: Response, url: String, isBodyFragment: Boolean = false): Document {
    val document = if (isBodyFragment) {
      response.body?.string()?.let { Jsoup.parseBodyFragment(it, url) }
    } else {
      response.body?.byteStream()?.use { Jsoup.parse(it, "UTF-8", url) }
    }
    return document ?: throw IOException("$url Body is empty")
  }

  private fun parseNovels(document: Document): List<DefaultNovelId> {
    return document.body().select(".chzn-select option[value*=https]").map { element ->
      DefaultNovelId(
        id = element.attr("value"),
        title = element.text())
    }
  }

  private fun parseChapters(document: Document): List<DefaultChapterId> {
    return document.select("a[data-id]").map {
      DefaultChapterId(
        id = it.attr("data-id"),
        title = it.text()
      )
    }
  }

  private fun retrievePostIdBy(novelId: DefaultNovelId): String {
    val response = client.newCall(Request.Builder()
      .head()
      .url(novelId.id)
      .build()
    ).execute()

    val (_, headerValue) = response.headers
      .firstOrNull { (key, value) ->
        "Link".equals(key, ignoreCase = true) && value.toLowerCase(Locale.ENGLISH).contains("?p=")
      } ?: throw IOException("Expected header is missing on $novelId")

    return headerValue.substringAfter("?p=").substringBefore(">").trim()
  }

  private fun retrieveChapterUrlBy(chapterId: DefaultChapterId): String {
    return "https://www.novelupdates.com/extnu/${chapterId.id}/"
  }
}

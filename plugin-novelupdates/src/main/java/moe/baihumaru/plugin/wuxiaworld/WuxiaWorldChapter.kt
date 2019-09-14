package moe.baihumaru.plugin.wuxiaworld

import moe.baihumaru.core.Chapter
import okhttp3.HttpUrl
import org.jsoup.nodes.Document

internal data class WuxiaWorldChapter(
  override val url: String,
  override val title: String,
  override val rawText: String?
) : Chapter {
  companion object {
    @JvmStatic
    fun from(document: Document, url: HttpUrl): WuxiaWorldChapter {
      return WuxiaWorldChapter(
        url = url.toString(),
        title = document.body().selectFirst("img[src*=title-icon.png] ~ h4")?.text()
          ?: "Title can't be parsed",
        rawText = document.body().select("#chapter-content").html()
      )
    }
  }
}
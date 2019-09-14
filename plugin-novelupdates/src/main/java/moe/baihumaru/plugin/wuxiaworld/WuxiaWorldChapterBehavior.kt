package moe.baihumaru.plugin.wuxiaworld

import moe.baihumaru.core.Chapter
import moe.baihumaru.core.ChapterBehavior
import okhttp3.HttpUrl
import org.jsoup.nodes.Document

internal data class WuxiaWorldChapterBehavior(
  override val nextChapter: String?,
  override val previousChapter: String?,
  override val isHtml: Boolean = false
) : ChapterBehavior {

  companion object {
    @JvmStatic
    fun from(document: Document, url: HttpUrl, parsedChapter: Chapter): WuxiaWorldChapterBehavior {
      with(document.body()) {
        return WuxiaWorldChapterBehavior(
          nextChapter = selectFirst("li .next a")?.attr("href"),
          previousChapter = selectFirst("li .prev a")?.attr("href"),
          isHtml = parsedChapter.rawText?.contains("<tr>") ?: false
        )
      }
    }
  }
}
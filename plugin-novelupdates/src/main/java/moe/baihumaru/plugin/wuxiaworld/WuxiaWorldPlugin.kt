package moe.baihumaru.plugin.wuxiaworld

import moe.baihumaru.core.Chapter
import moe.baihumaru.core.ChapterBehavior
import moe.baihumaru.core.PluginFeature
import moe.baihumaru.core.PluginFeature.COMPLETE_NOVEL_LIST
import moe.baihumaru.core.PluginFeature.NAVIGABLE_CHAPTER
import moe.baihumaru.plugin.novelupdates.NovelUpdatesPlugin
import okhttp3.HttpUrl
import org.jsoup.nodes.Document

class WuxiaWorldPlugin : NovelUpdatesPlugin() {
  override fun features(): Set<PluginFeature> {
    return setOf(
      COMPLETE_NOVEL_LIST,
      NAVIGABLE_CHAPTER
    )
  }

  override val groupNameId = "wuxiaworld"

  override val displayName = "WuxiaWorld"

  private val host = "www.wuxiaworld.com"

  override val homeUrl = "https://$host"

  override fun canHandle(url: HttpUrl): Boolean {
    return host.equals(url.host, ignoreCase = true)
  }

  override fun parseChapter(document: Document, chapterUrl: HttpUrl): Chapter {
    return WuxiaWorldChapter.from(document, chapterUrl)
  }

  override fun parseChapterBehavior(document: Document, chapterUrl: HttpUrl, parsedChapter: Chapter): ChapterBehavior {
    return WuxiaWorldChapterBehavior.from(document, chapterUrl, parsedChapter)
  }
}

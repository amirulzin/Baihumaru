package moe.baihumaru.core

data class DefaultChapterBehavior(
  override val nextChapter: String? = null,
  override val previousChapter: String? = null,
  override val isDisplayTextCustomized: Boolean = false,
  override val order: Long = ChapterBehavior.UNKNOWN_ORDER,
  override val isTitleSpoiler: Boolean = false,
  override val locked: Boolean = false,
  override val mustLoadViaWebView: Boolean = false,
  override val isHtml: Boolean = false
) : ChapterBehavior
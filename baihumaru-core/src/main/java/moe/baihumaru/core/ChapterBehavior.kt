package moe.baihumaru.core

interface ChapterBehavior {
  val nextChapter: String? get() = null
  val previousChapter: String? get() = null
  val isDisplayTextCustomized: Boolean get() = false
  fun displayText(rawText: String?): String? = rawText
  val order: Long get() = UNKNOWN_ORDER
  val isTitleSpoiler: Boolean get() = false
  val locked: Boolean get() = false
  val mustLoadViaWebView: Boolean get() = false
  val isHtml: Boolean get() = false

  companion object {
    const val UNKNOWN_ORDER = -1L
  }
}
package moe.baihumaru.core

data class DefaultChapterId(
  override val id: String,
  override val title: String,
  override val isTitleSpoiler: Boolean = false
) : ChapterId
package moe.baihumaru.core

data class DefaultNovelId(
  override val id: String,
  override val title: String,
  override val tags: Set<String> = emptySet()
) : NovelId
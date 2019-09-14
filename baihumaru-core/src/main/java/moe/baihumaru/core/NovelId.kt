package moe.baihumaru.core

interface NovelId {
  val id: String
  val title: String
  val tags: Set<String>
}
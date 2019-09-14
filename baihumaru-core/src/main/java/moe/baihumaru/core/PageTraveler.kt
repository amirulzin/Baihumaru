package moe.baihumaru.core

data class PageTraveler(val searchQuery: String = "", val currentPage: Int = -1) {
  fun isUninitialized() = currentPage == -1
}
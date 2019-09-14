package moe.baihumaru.android.navigation

import commons.android.core.navigation.NavigationRoot
import moe.baihumaru.android.R

interface PrimaryNavRoot : NavigationRoot {
  override fun contentId() = R.id.content
}
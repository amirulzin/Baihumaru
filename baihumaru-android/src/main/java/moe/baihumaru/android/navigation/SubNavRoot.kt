package moe.baihumaru.android.navigation

import commons.android.core.navigation.NavigationRoot
import moe.baihumaru.android.R

interface SubNavRoot : NavigationRoot {
  override fun contentId() = R.id.subContent
}
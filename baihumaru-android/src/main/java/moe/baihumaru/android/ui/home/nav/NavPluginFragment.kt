package moe.baihumaru.android.ui.home.nav

import moe.baihumaru.android.R
import moe.baihumaru.android.ui.plugins.PluginsFragment

class NavPluginFragment : HomeNavigationFragment<PluginsFragment>() {
  override fun initialFragmentConstructor() = PluginsFragment.newInstance()
  override fun navId() = R.id.nav_plugins
}
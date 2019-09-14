package commons.android.core.activity

import android.view.Gravity
import androidx.annotation.IdRes
import androidx.drawerlayout.widget.DrawerLayout

interface DrawerActivity {
  val contentFrameId: Int
  fun getDrawerLayout(): DrawerLayout
  fun bindNavigationListener()
  fun initDrawerNav(@IdRes id: Int, commit: Boolean)
  fun toggleSidebar(enabled: Boolean)
  fun toggleBottomNav(enabled: Boolean)
  fun showDrawer(gravity: Int = Gravity.START)
}
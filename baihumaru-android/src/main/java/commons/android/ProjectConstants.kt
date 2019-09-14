package commons.android

import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerAppCompatDialogFragment
import dagger.android.support.DaggerFragment
import moe.baihumaru.android.BuildConfig
import moe.baihumaru.android.R

object ProjectConstants {
  const val STR_DRAWER_OPEN = -1 //R.string.drawer_open
  const val STR_DRAWER_CLOSE = -1 //R.string.drawer_close
  const val LYT_BASE_DRAWER = -1 // R.layout.activity_base_drawer
  const val LYT_BASE_DRAWER_CONTENT_FRAME = -1 //R.id.drawerContentFrame
  const val CLR_PRIMARY = R.color.colorPrimary
  const val CLR_ACCENT = R.color.colorAccent
  const val LYT_FRAGMENT_RECYCLER_VIEW = -1 // R.layout.fragment_recycler
  const val APP_THEME = -1 //R.style.AppTheme
  const val SHADOW_85 = -1 //R.color.pcc_shadow_85
  const val DEBUG_TAG = "DEBUG_TAG"
}

typealias BuildConfigAlias = BuildConfig
typealias RootActivityAlias = DaggerAppCompatActivity
typealias DialogFragmentAlias = DaggerAppCompatDialogFragment
typealias FragmentAlias = DaggerFragment
//typealias DrawerActivityBindingAlias = ActivityBaseDrawerBinding
//typealias DrawerFragmentRecyclerBindingAlias = FragmentRecyclerBinding
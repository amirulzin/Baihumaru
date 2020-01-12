package moe.baihumaru.android.ui.home.nav

import moe.baihumaru.android.R
import moe.baihumaru.android.ui.library.LibraryFragment

class NavLibraryFragment : HomeNavigationFragment<LibraryFragment>() {
  override fun initialFragmentConstructor() = LibraryFragment.newInstance()
  override fun navId() = R.id.nav_library
}
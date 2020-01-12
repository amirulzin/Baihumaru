package moe.baihumaru.android.ui.home.nav

import moe.baihumaru.android.R
import moe.baihumaru.android.ui.catalogues.CataloguesFragment

class NavCataloguesFragment : HomeNavigationFragment<CataloguesFragment>() {
  override fun initialFragmentConstructor() = CataloguesFragment.newInstance()
  override fun navId() = R.id.nav_catalogues
}
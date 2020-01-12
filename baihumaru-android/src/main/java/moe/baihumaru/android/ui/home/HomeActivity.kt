package moe.baihumaru.android.ui.home

import android.os.Bundle
import commons.android.arch.viewModelOf
import commons.android.dagger.arch.DaggerViewModelFactory
import moe.baihumaru.android.databinding.HomeActivityBinding
import moe.baihumaru.android.ui.defaults.CoreActivity
import moe.baihumaru.android.ui.home.crumbs.CrumbViewModel
import moe.baihumaru.android.ui.home.init.HomeInitViewModel
import moe.baihumaru.android.ui.home.nav.HomeNavigationFragment
import moe.baihumaru.android.ui.home.nav.NavViewModel
import javax.inject.Inject

class HomeActivity : CoreActivity() {

  @Inject
  lateinit var homeVMF: DaggerViewModelFactory<HomeInitViewModel>
  @Inject
  lateinit var crumbVMF: DaggerViewModelFactory<CrumbViewModel>
  @Inject
  lateinit var navVMF: DaggerViewModelFactory<NavViewModel>

  private lateinit var binding: HomeActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = HomeActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)

    HomeConstruct(
      activity = this,
      binding = binding,
      initVM = viewModelOf(HomeInitViewModel::class.java, homeVMF),
      crumbsVM = viewModelOf(CrumbViewModel::class.java, crumbVMF),
      navVM = viewModelOf(NavViewModel::class.java, navVMF)
    ).init(savedInstanceState)
  }

  override fun onBackPressed() {
    val handled = handleChildBackStack()
    if (!handled) {
      if (supportFragmentManager.backStackEntryCount > 0) {
        supportFragmentManager.popBackStack()
      } else {
        super.onBackPressed()
      }
    }
  }

  private fun handleChildBackStack(): Boolean {
    val menuItem = with(binding.bottomNav) {
      menu.findItem(selectedItemId)
    }

    val navFragment = supportFragmentManager.findFragmentByTag(menuItem.title.toString())

    if (navFragment is HomeNavigationFragment<*>) {
      val cfm = navFragment.childFragmentManager
      if (cfm.backStackEntryCount > 0) {
        cfm.popBackStack()
        return true
      }
    }
    return false
  }
}
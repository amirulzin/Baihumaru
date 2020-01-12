package moe.baihumaru.android.ui.home

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import commons.android.arch.UICompositeConstruct
import commons.android.arch.UIConstruct
import commons.android.arch.annotations.ViewLayer
import commons.android.arch.observeNonNull
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.HomeActivityBinding
import moe.baihumaru.android.ui.home.crumbs.CrumbViewModel
import moe.baihumaru.android.ui.home.init.HomeInitViewModel
import moe.baihumaru.android.ui.home.init.UIHomeInit
import moe.baihumaru.android.ui.home.nav.*

@ViewLayer
class HomeConstruct(
  private val activity: HomeActivity,
  private val binding: HomeActivityBinding,
  private val initVM: HomeInitViewModel,
  private val crumbsVM: CrumbViewModel,
  private val navVM: NavViewModel
) : UICompositeConstruct {


  // This is needed since setting BottomNav default selectedItemId for some reason induced
  // a reselection bug (it only initialize the first ever selection by actually re-selecting the item!)
  //
  // Thus, while it works in many other projects, if we don't want the reselection behavior e.g adding
  // setOnNavigationItemReselectedListener { /* no-op */ },
  //
  // this actually will fail to initialize the first selection every time no matter how many View.post {}
  // hacks we want to invoke (I even tried nested View.posts!)
  //
  // Hence the temporary solution is actually storing the selectionId as per below
  // and check them during nav and avoid using setOnNavigationItemReselectedListener altogether

  private var freshInstance: Boolean = true

  private val initConstruct = object : UIConstruct<UIHomeInit> {
    override fun init(savedInstanceState: Bundle?) {
      initVM.homeLive.observeNonNull(activity, ::bindUpdates)
      freshInstance = savedInstanceState == null

      binding.bottomNav.setOnNavigationItemSelectedListener(navListener)
    }

    override fun bindUpdates(data: UIHomeInit) {
      if (freshInstance) {
        binding.bottomNav.post {
          binding.bottomNav.selectedItemId = R.id.nav_library
        }
      }
    }
  }

  private val crumbConstruct = object : UIConstruct<String> {
    override fun init(savedInstanceState: Bundle?) {
      crumbsVM.crumbsLive.observeNonNull(activity, ::bindUpdates)
    }

    override fun bindUpdates(data: String) {
      binding.title.text = data
    }
  }

  private val navListener = object : BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
      val itemId = item.itemId
      if (navVM.selectionLive.value != itemId) {
        navBy(item)
        navVM.selectionLive.value = itemId
      }
      return true
    }
  }

  override fun init(savedInstanceState: Bundle?) {
    initConstruct.init(savedInstanceState)
    crumbConstruct.init(savedInstanceState)
  }

  private fun navBy(item: MenuItem) {
    val fm = activity.supportFragmentManager
    val fragmentTag = item.title.toString()
    val containerId = binding.content.id

    val applicableFragments = fm.fragments.filterIsInstance<HomeNavigationFragment<*>>()
    val (targetFragment, otherFragments) = applicableFragments
      .partition { it.fragmentTag() == fragmentTag }
      .let { (targets, others) ->
        require(targets.size <= 1) { "Multiple fragments with the tag: $fragmentTag were added to fragment manager!" }
        targets.firstOrNull() to others
      }

    if (targetFragment == null) {
      with(fm.beginTransaction()) {
        when (item.itemId) {
          R.id.nav_library -> NavLibraryFragment()
          R.id.nav_catalogues -> NavCataloguesFragment()
          R.id.nav_plugins -> NavPluginFragment()
          else -> null
        }?.let { target ->
          target.applyNewInstanceArgs(fragmentTag)
          add(containerId, target, fragmentTag)
          otherFragments.map(::hide)
          show(target)
          commit()
        }
      }
    } else {
      with(fm.beginTransaction()) {
        show(targetFragment)
        otherFragments.map(::hide)
        commit()
      }
    }
  }
}
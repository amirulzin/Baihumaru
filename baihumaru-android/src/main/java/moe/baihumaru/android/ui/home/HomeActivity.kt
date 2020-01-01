package moe.baihumaru.android.ui.home

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import commons.android.arch.RxViewModel
import commons.android.arch.UIConstruct
import commons.android.arch.observeNonNull
import commons.android.arch.offline.RxResourceLiveData
import commons.android.arch.offline.State
import commons.android.arch.viewModelOf
import commons.android.core.inset.StatusBarUtil
import commons.android.dagger.arch.DaggerViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.HomeActivityBinding
import moe.baihumaru.android.ui.catalogues.CataloguesFragment
import moe.baihumaru.android.ui.library.LibraryFragment
import moe.baihumaru.android.ui.plugins.PluginsFragment
import javax.inject.Inject

class HomeActivity : DaggerAppCompatActivity() {

  @Inject
  lateinit var vmf: DaggerViewModelFactory<HomeViewModel>
  lateinit var binding: HomeActivityBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    StatusBarUtil.toggleStatusBar(this, true, R.color.colorTransparent)
    binding = HomeActivityBinding.inflate(layoutInflater)
    setContentView(binding.root)
    HomeConstruct(
      activity = this,
      vm = viewModelOf(vmf, HomeViewModel::class.java),
      binding = binding
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

class HomeConstruct(
  private val activity: AppCompatActivity,
  private val vm: HomeViewModel,
  private val binding: HomeActivityBinding
) : UIConstruct<UIHome> {
  @SuppressLint("RestrictedApi")
  override fun init(savedInstanceState: Bundle?) {
    vm.homeLive.observeNonNull(activity, ::bindUpdates)
    if (savedInstanceState == null) {
      binding.root.post {
        binding.bottomNav.selectedItemId = R.id.nav_library
      }
    }
  }

  override fun bindUpdates(data: UIHome) {
    binding.bottomNav.setOnNavigationItemSelectedListener { item ->
      binding.root.post { navBy(item) }
      true
    }
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
        }

        commit()
      }
    } else {
      with(fm.beginTransaction()) {
        show(targetFragment)
        otherFragments.map(::hide)
        commit()
      }
    }
  }

  class NavLibraryFragment : HomeNavigationFragment<LibraryFragment>() {
    override fun fragmentConstructor() = LibraryFragment.newInstance()
  }

  class NavCataloguesFragment : HomeNavigationFragment<CataloguesFragment>() {
    override fun fragmentConstructor() = CataloguesFragment.newInstance()
  }

  class NavPluginFragment : HomeNavigationFragment<PluginsFragment>() {
    override fun fragmentConstructor() = PluginsFragment.newInstance()
  }
}

data class UIHome(val appState: AppState)

data class AppState(val isFirstLoad: Boolean)

class HomeViewModel @Inject constructor(val homeLive: HomeLive) : RxViewModel(homeLive.disposables)

class HomeLive @Inject constructor(private val prefs: SharedPreferences) : RxResourceLiveData<UIHome>() {
  override fun onActive() {
    super.onActive()
    if (resourceState.value.state == State.READY) {
      Single.fromCallable { loadDefaults(prefs) }
        .subscribeOn(Schedulers.io())
        .doFinally { postComplete() }
        .subscribe(::postValue)
        .addTo(disposables)
    }
  }

  companion object {
    @WorkerThread
    @JvmStatic
    private fun loadDefaults(prefs: SharedPreferences): UIHome {
      return UIHome(appState = AppState(
        isFirstLoad = prefs.getBoolean("isFirstLoad", true)
          .also { prefs.edit().putBoolean("isFirstLoad", false).apply() }
      ))
    }
  }
}
package moe.baihumaru.android.ui.defaults

import androidx.viewbinding.ViewBinding
import commons.android.arch.viewModelOf
import commons.android.dagger.compat.DaggerActivity
import commons.android.viewbinding.ViewBindingFragment
import moe.baihumaru.android.ui.home.crumbs.CrumbViewModel
import moe.baihumaru.android.ui.home.nav.NavViewModel

abstract class CoreActivity : DaggerActivity()

interface TitledFragment {
  val contextualTitle: String
}

abstract class CoreParentFragment<V : ViewBinding> : ViewBindingFragment<V>() {
  abstract fun navId(): Int

  override fun onHiddenChanged(hidden: Boolean) {
    super.onHiddenChanged(hidden)
    if (!hidden) {
      switchCrumbs()
    }
  }

  private fun switchCrumbs() {
    val currentActivity = activity
    if (currentActivity is CoreActivity) {
      with(currentActivity.viewModelOf(CrumbViewModel::class.java)) {
        switchCrumbRoot(navId())
      }
    }
  }
}

abstract class CoreNestedFragment<V : ViewBinding> : ViewBindingFragment<V>(), TitledFragment {

  override fun onResume() {
    super.onResume()
    updateCrumbs()
  }

  private fun updateCrumbs() {
    val currentActivity = activity
    if (currentActivity is CoreActivity) {
      with(currentActivity.viewModelOf(NavViewModel::class.java)) {
        val parentId = resolveParentId()
        if (parentId == selectionLive.value) {
          with(currentActivity.viewModelOf(CrumbViewModel::class.java)) {
            updateCurrentCrumb(parentId, contextualTitle)
          }
        }
      }
    }
  }


  private fun resolveParentId(): Int {
    return requireNotNull(parentFragment) { "${this::class.qualifiedName} is directly connected to an activity thus does not have any parent fragment!" }
      .let { parent ->
        (parent as? CoreParentFragment<*>)
          ?: throw IllegalStateException("Wrong parent fragment. Required: ${CoreParentFragment::class.simpleName}")
      }.navId()
  }
}
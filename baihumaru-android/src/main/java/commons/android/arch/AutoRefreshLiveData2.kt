package commons.android.arch

import androidx.annotation.CallSuper
import androidx.lifecycle.MediatorLiveData
import commons.android.arch.offline.refresh.RefreshDelegate
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate")
abstract class AutoRefreshLiveData2<T> : MediatorLiveData<T>() {

  private val refreshDelegate = RefreshDelegate(10, TimeUnit.MINUTES)

  protected open fun shouldRefresh() = refreshDelegate.shouldRefresh()

  fun resetRefreshFlags() {
    refreshDelegate.resetRefreshFlags()
  }

  @CallSuper
  override fun onActive() {
    super.onActive()
    refreshOptionally()
  }

  @CallSuper
  fun refreshOptionally() {
    if (shouldRefresh()) {
      refreshDelegate.updateLastRefresh()
      fetch()
    }
  }

  fun forceRefresh() {
    resetRefreshFlags()
    refreshDelegate.updateLastRefresh()
    fetch()
  }

  abstract fun fetch()
}
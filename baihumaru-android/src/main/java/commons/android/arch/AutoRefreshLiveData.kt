package commons.android.arch

import androidx.annotation.CallSuper
import commons.android.arch.offline.RxResourceLiveData
import commons.android.arch.offline.refresh.RefreshDelegate
import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate")
abstract class AutoRefreshLiveData<T : Any>(protected val errorHandler: RetrofitRxErrorHandler) : RxResourceLiveData<T>() {

  init {
    errorHandler.postMessage = ::postErrorRemote
  }

  private val refreshDelegate = RefreshDelegate(10, TimeUnit.MINUTES)

  protected open fun shouldRefresh() = refreshDelegate.shouldRefresh()

  protected open fun clearOnInactive() = false

  fun resetRefreshFlags() {
    refreshDelegate.resetRefreshFlags()
  }

  @CallSuper
  override fun onActive() {
    super.onActive()
    if (shouldRefresh()) refresh()
  }

  @CallSuper
  open fun refresh() {
    postLoading()
    refreshDelegate.updateLastRefresh()
    clearDisposables()
  }

  fun forceRefresh() {
    resetRefreshFlags()
    refresh()
  }

  @CallSuper
  open fun updateRemote() {
    postLoading()
    refreshDelegate.updateLastRefresh()
    clearDisposables()
  }


  override fun setValue(value: T) {
    super.setValue(value)
    postComplete()
  }

  override fun postValue(value: T) {
    super.postValue(value)
    postComplete()
  }

  @CallSuper
  override fun onInactive() {
    super.onInactive()
    if (clearOnInactive()) clearDisposables()
  }
}
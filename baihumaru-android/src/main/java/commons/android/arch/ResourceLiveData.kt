package commons.android.arch

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import commons.android.arch.offline.NonNullObserver
import commons.android.arch.offline.ResourceState
import commons.android.arch.offline.State
import javax.inject.Inject

class ResourceLiveData @Inject constructor() : MediatorLiveData<ResourceState>() {
  init {
    setReady()
  }

  fun postLoading() {
    postValue(ResourceState(State.LOADING))
  }

  fun postComplete(unused: Any?) {
    postValue(ResourceState(State.READY))
  }

  fun postError(error: Throwable?) {
    postValue(ResourceState(State.ERROR, error?.message
      ?: "Unknown error"))
  }

  fun setReady() {
    value = ResourceState(State.READY)
  }

  fun postReady() {
    postValue(ResourceState(State.READY))
  }

  fun observeConsuming(owner: LifecycleOwner, nonNullObserver: NonNullObserver<ResourceState>) {
    observe(owner) { resourceState ->
      nonNullObserver.onChanged(resourceState)
      if (State.READY != resourceState?.state) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) setReady()
        else postReady()
      }
    }
  }

  fun isNotLoading(): Boolean {
    return State.LOADING != value?.state
  }
}
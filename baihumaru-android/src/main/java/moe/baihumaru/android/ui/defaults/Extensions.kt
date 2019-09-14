package moe.baihumaru.android.ui.defaults

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import commons.android.arch.AutoRefreshLiveData
import commons.android.arch.observeNonNull
import commons.android.arch.offline.RxResourceLiveData
import commons.android.arch.offline.State

fun SwipeRefreshLayout.bindRefresh(owner: LifecycleOwner, autoRefreshLiveData: AutoRefreshLiveData<*>) {
  setOnRefreshListener(autoRefreshLiveData::forceRefresh)
  bindLoading(owner, autoRefreshLiveData)
}

fun SwipeRefreshLayout.bindLoading(owner: LifecycleOwner, resourceLiveData: RxResourceLiveData<*>) {
  resourceLiveData.resourceState.observeNonNull(owner) { resourceState ->
    isRefreshing = resourceState.state == State.LOADING
  }
}
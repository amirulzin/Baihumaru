package moe.baihumaru.android.ui.defaults

import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import commons.android.arch.AutoRefreshLiveData
import commons.android.arch.AutoRefreshLiveData2
import commons.android.arch.ResourceLiveData
import commons.android.arch.observeNonNull
import commons.android.arch.offline.NonNullObserver
import commons.android.arch.offline.ResourceState
import commons.android.arch.offline.RxResourceLiveData
import commons.android.arch.offline.State

fun SwipeRefreshLayout.bindRefresh(owner: LifecycleOwner, autoRefreshLiveData: AutoRefreshLiveData<*>) {
  setOnRefreshListener(autoRefreshLiveData::forceRefresh)
  bindLoading(owner, autoRefreshLiveData)
}

fun SwipeRefreshLayout.bindRefresh(owner: LifecycleOwner, autoRefreshLiveData: AutoRefreshLiveData2<*>, stateLiveData: ResourceLiveData) {
  setOnRefreshListener(autoRefreshLiveData::forceRefresh)
  bindLoading(owner, stateLiveData)
}

fun SwipeRefreshLayout.bindLoading(owner: LifecycleOwner, resourceLiveData: ResourceLiveData) {
  resourceLiveData.observeConsuming(owner, object : NonNullObserver<ResourceState> {
    override fun onValueChanged(data: ResourceState) {
      isRefreshing = data.state == State.LOADING
    }
  })
}

fun SwipeRefreshLayout.bindLoading(owner: LifecycleOwner, resourceLiveData: RxResourceLiveData<*>) {
  resourceLiveData.resourceState.observeNonNull(owner) { resourceState ->
    isRefreshing = resourceState.state == State.LOADING
  }
}
package commons.android.arch.offline.refresh

import commons.android.arch.offline.ResourceState
import commons.android.arch.offline.ResourceType
import commons.android.arch.offline.State.*

/**
 * Handles ResourceState updates to relevant views.
 *
 * This also assume that all LOADING state is also executing a remote call.
 */
object ResourceStateHandler {

  @JvmStatic
  fun handleStateUpdates(
    coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
    refreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout,
    resourceState: ResourceState,
    remoteRefreshable: RemoteRefreshable
  ) {
    with(refreshLayout) {
      when (resourceState.state) {
        READY -> isRefreshing = false
        COMPLETE -> isRefreshing = false
        LOADING -> isRefreshing = true
        ERROR -> {
          isRefreshing = false
          com.google.android.material.snackbar.Snackbar.make(coordinatorLayout, resourceState.message, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show()
        }
      }
    }
    with(resourceState) {
      when {
        state == LOADING -> remoteRefreshable.showRemoteIndicator(true)
        state != LOADING && type == ResourceType.REMOTE -> remoteRefreshable.showRemoteIndicator(false)
      }
    }
  }
}
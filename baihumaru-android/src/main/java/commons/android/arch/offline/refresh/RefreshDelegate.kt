package commons.android.arch.offline.refresh

import java.util.concurrent.TimeUnit

class RefreshDelegate(refreshThreshold: Long, unit: TimeUnit) {
  private var lastRefreshed = 0L

  private val refreshThresholdMillis = TimeUnit.MILLISECONDS.convert(refreshThreshold, unit)

  fun shouldRefresh() = lastRefreshed == 0L
    || System.currentTimeMillis() - lastRefreshed >= refreshThresholdMillis

  fun resetRefreshFlags() {
    lastRefreshed = 0L
  }

  fun updateLastRefresh() {
    lastRefreshed = System.currentTimeMillis()
  }
}
package commons.android.offline

import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.util.concurrent.atomic.AtomicBoolean


class OfflineController(private val cm: ConnectivityManager?) {
  private val isOfflineRef = AtomicBoolean(true)

  fun isOffline(): Boolean = isOfflineRef.get()

  fun toggleOffline(isOffline: Boolean? = null) {
    if (isOffline == null) {
      isOfflineRef.set(!isOfflineRef.get())
    } else isOfflineRef.set(isOffline)
  }

  fun shouldBeOffline(): Boolean {
    if (isOffline()) {
      return true
    } else {
      return cm?.activeNetworkInfo?.isNotConnected() ?: return false
    }
  }

  private fun NetworkInfo.isNotConnected() = !isConnected
}
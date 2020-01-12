package commons.android.core.threading

import android.os.Looper

object MultiThreadingUtils {
  @JvmStatic
  fun isMainThread(): Boolean {
    return Thread.currentThread() == Looper.getMainLooper().thread
  }

  @JvmStatic
  fun isWorkerThread(): Boolean = !isMainThread()

  @JvmStatic
  fun requireMainThread() {
    require(isMainThread())
  }

  @JvmStatic
  fun requireWorkerThread() {
    require(isWorkerThread())
  }

  @JvmStatic
  inline fun <T> requireWorkerThread(crossinline block: () -> T): T {
    require(isWorkerThread())
    return block.invoke()
  }
}
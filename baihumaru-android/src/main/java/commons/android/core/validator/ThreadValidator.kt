package commons.android.core.validator

import android.os.Looper
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import java.io.IOException

/**
 * Simple thread validator for Android
 */
object ThreadValidator {

  @JvmStatic
  @WorkerThread
  fun validateWorkerThread() {
    if (Thread.currentThread() == Looper.getMainLooper().thread) throw IOException("Call invoked on Main Thread")
  }

  @JvmStatic
  @MainThread
  fun validateMainThread() {
    if (Thread.currentThread() != Looper.getMainLooper().thread) throw IOException("Call not invoked on main thread")
  }
}

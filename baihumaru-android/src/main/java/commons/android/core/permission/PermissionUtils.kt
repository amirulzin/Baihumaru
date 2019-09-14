package commons.android.core.permission

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

inline fun androidx.fragment.app.Fragment.checkPermission(requestCode: Int, permission: String, crossinline onAlreadyGranted: () -> Unit) {
  activity?.let { act ->
    if (ContextCompat.checkSelfPermission(act, permission) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(permission), requestCode)
    } else {
      onAlreadyGranted()
    }
  }
}
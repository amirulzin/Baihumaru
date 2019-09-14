package commons.android.core.permission

abstract class PermissionHandler(
  private val fragment: androidx.fragment.app.Fragment,
  private val requestCode: Int,
  private val permission: String
) {

  abstract fun onGranted()

  fun handleRequest(bypass: Boolean) {
    if (bypass) onGranted()
    else {
      fragment.checkPermission(requestCode, permission, ::onGranted)
    }
  }
}
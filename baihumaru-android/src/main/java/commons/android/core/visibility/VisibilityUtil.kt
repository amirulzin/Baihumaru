package commons.android.core.visibility

import android.view.View

inline fun View.autoVisibility(isVisible: Boolean, crossinline executeIfVisible: () -> Unit) {
  if (isVisible) {
    visibility = View.VISIBLE
    executeIfVisible()
  } else {
    visibility = View.GONE
  }
}

inline fun <T : Any?> View.autoHideOnNull(target: T?, crossinline executeIfVisible: (T) -> Unit = {}) {
  if (target != null) {
    visibility = View.VISIBLE
    executeIfVisible(target)
  } else {
    visibility = View.GONE
  }
}
package commons.android.core.fullscreen

import android.app.Activity
import android.view.WindowManager

object FullScreenUtil {

  @JvmStatic
  fun exitFullScreen(activity: Activity?) {
    activity?.window?.apply {
      clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
  }

  @JvmStatic
  fun enterFullscreen(activity: Activity?) {
    activity?.window?.apply {
      setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
  }
}
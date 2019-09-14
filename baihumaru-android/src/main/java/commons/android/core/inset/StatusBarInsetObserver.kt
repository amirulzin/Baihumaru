package commons.android.core.inset

import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * Observer to toggle status bar be fullscreen or not during onResume.
 *
 *
 * Should only exist on View layer (Activity and Fragment).
 */
data class StatusBarInsetObserver constructor(
  private val activity: AppCompatActivity?,
  private val isFullscreen: Boolean,
  @ColorRes private val statusBarColorRes: Int,
  private val isLightTheme: Boolean?
) : LifecycleObserver {

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  fun toggleFullScreenOnResume() {
    if (activity != null) {
      StatusBarUtil.toggleStatusBar(activity, isFullscreen, statusBarColorRes)
      if (isLightTheme != null) {
        StatusBarUtil.setStatusBarLightTheme(activity, isLightTheme)
      }
    }
  }

  companion object {
    @JvmStatic
    fun plant(
      owner: LifecycleOwner,
      activity: AppCompatActivity?,
      isFullscreen: Boolean,
      @ColorRes statusBarColorRes: Int
    ) {
      owner.lifecycle.addObserver(StatusBarInsetObserver(activity, isFullscreen, statusBarColorRes, null))
    }

    @JvmStatic
    fun plant(
      owner: LifecycleOwner,
      activity: AppCompatActivity?,
      isFullscreen: Boolean,
      @ColorRes statusBarColorRes: Int,
      isLightTheme: Boolean
    ) {
      owner.lifecycle.addObserver(StatusBarInsetObserver(activity, isFullscreen, statusBarColorRes, isLightTheme))
    }
  }
}

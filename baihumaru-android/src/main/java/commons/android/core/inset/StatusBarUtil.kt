package commons.android.core.inset

import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import commons.android.core.flags.FlagResult

object StatusBarUtil {

  @JvmStatic
  fun toggleStatusBar(
    activity: AppCompatActivity?,
    fullScreen: Boolean,
    @ColorRes statusBarColorRes: Int
  ) {
    if (activity != null) {
      val decorView = activity.window.decorView
      val flags = decorView.systemUiVisibility
      val (applied, resultFlags) = FlagResult.toggleFlagsBy(fullScreen, flags,
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
      if (applied) {
        decorView.systemUiVisibility = resultFlags
        setStatusBarColor(activity, statusBarColorRes)
      }
    }
  }

  @JvmStatic
  fun setStatusBarColor(activity: AppCompatActivity?, @ColorRes statusBarColorRes: Int) {
    if (activity != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.window.statusBarColor = ContextCompat.getColor(activity, statusBarColorRes)
      }
    }
  }

  @JvmStatic
  fun setStatusBarColorActual(activity: AppCompatActivity?, @ColorInt statusBarColor: Int) {
    if (activity != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        activity.window.statusBarColor = statusBarColor
      }
    }
  }

  @JvmStatic
  fun setStatusBarLightTheme(activity: AppCompatActivity?, lightTheme: Boolean) {
    if (activity != null) {
      val decorView = activity.window
        .decorView
      val flags = decorView.systemUiVisibility
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          val (applied, resultFlags) = FlagResult.toggleFlagsBy(lightTheme, flags,
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
          if (applied)
            decorView.systemUiVisibility = resultFlags
        } else {

        }
      }
    }
  }
}

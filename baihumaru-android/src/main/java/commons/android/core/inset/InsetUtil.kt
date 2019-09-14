package commons.android.core.inset

import android.annotation.SuppressLint
import android.os.Build
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import java.util.*

/**
 * Refer https://chris.banes.dev/2019/04/12/insets-listeners-to-layouts/
 */
interface InsetUtil {

  interface Internal {
    companion object {
      fun createInsetDirection(gravities: IntArray): Set<Int> {
        val set = HashSet<Int>(gravities.size)
        for (gravity in gravities) {
          set.add(gravity)
        }
        return set
      }

      @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
      fun applyInsetOnAttached(target: View) {
        if (target.isAttachedToWindow) {
          target.requestApplyInsets()
        } else {
          target.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
              v.removeOnAttachStateChangeListener(this)
              v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) {}
          })
        }
      }
    }
  }

  companion object {

    @SuppressLint("ObsoleteSdkInt")
    fun applySystemInsetPadding(target: View, vararg gravities: Int) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

        val set = Internal.createInsetDirection(gravities)

        val paddingLeft = target.paddingLeft
        val paddingTop = target.paddingTop
        val paddingRight = target.paddingRight
        val paddingBottom = target.paddingBottom

        target.setOnApplyWindowInsetsListener { v, insets ->
          val left = if (set.contains(Gravity.LEFT)) paddingLeft + insets.systemWindowInsetLeft else paddingLeft
          val top = if (set.contains(Gravity.TOP)) paddingTop + insets.systemWindowInsetTop else paddingTop
          val right = if (set.contains(Gravity.RIGHT)) paddingRight + insets.systemWindowInsetRight else paddingRight
          val bottom = if (set.contains(Gravity.BOTTOM)) paddingBottom + insets.systemWindowInsetBottom else paddingBottom

          v.setPadding(left, top, right, bottom)

          insets
        }

        Internal.applyInsetOnAttached(target)
      }
    }
  }
}

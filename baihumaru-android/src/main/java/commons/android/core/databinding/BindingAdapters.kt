package commons.android.core.databinding

import android.annotation.TargetApi
import android.os.Build
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter

/**
 * Helper adapters for simple layout attributes
 */
object BindingAdapters {

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  @JvmStatic
  @BindingAdapter("compoundTint")
  fun tintCompoundDrawables(button: Button, @ColorInt tintColor: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      for (drawable in button.compoundDrawables) {
        drawable?.setTint(tintColor)
      }
  }
}
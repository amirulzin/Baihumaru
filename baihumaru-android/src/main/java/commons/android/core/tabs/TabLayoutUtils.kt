package commons.android.core.tabs

import androidx.annotation.ColorRes
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.tabs.TabLayout

@UiThread
fun TabLayout.tintIcon(@ColorRes colorRes: Int) {
  ContextCompat.getColorStateList(context, colorRes)?.let { colors ->
    for (i in 0 until tabCount) {
      getTabAt(i)?.icon?.let { drawable ->
        DrawableCompat.wrap(drawable)
        DrawableCompat.setTintList(drawable, colors)
      }
    }
  }
}

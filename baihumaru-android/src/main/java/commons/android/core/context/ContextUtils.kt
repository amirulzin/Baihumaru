package commons.android.core.context

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import kotlin.math.roundToInt

object ContextUtils {

  /**
   * Get pixels from the given dp (rounded via {@link Math#round})
   */
  @JvmStatic
  fun getPixels(context: Context, dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).roundToInt()
  }

  /**
   * Get dp from the given pixel (rounded via {@link Math#round})
   */
  @JvmStatic
  fun getDp(context: Context, px: Int): Int {
    return (px / context.resources.displayMetrics.density).roundToInt()
  }

  @JvmStatic
  fun isNightMode(context: Context): Boolean {
    return (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
  }
}
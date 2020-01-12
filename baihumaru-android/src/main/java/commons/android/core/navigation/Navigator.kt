package commons.android.core.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

inline fun FragmentManager.navInto(@IdRes containerId: Int, fragmentTag: String, crossinline fragmentFactory: (() -> Fragment)) {
  val outFrag = findFragmentByTag(fragmentTag) ?: fragmentFactory.invoke()
  beginTransaction()
    .replace(containerId, outFrag, fragmentTag)
    .commit()
}

inline fun FragmentManager.navIntoHistorically(@IdRes containerId: Int, fragmentTag: String, backStackTag: String? = null, crossinline fragmentFactory: (() -> Fragment)) {
  val outFrag = findFragmentByTag(fragmentTag) ?: fragmentFactory.invoke()
  beginTransaction()
    .replace(containerId, outFrag, fragmentTag)
    .addToBackStack(backStackTag)
    .commit()
}

interface NavigationRoot {
  @IdRes
  fun contentId(): Int
}

inline fun NavigationRoot.navIntoHistorically(fragmentTag: String, backStackTag: String? = null, crossinline fragmentFactory: (() -> Fragment)) {
  (this as Fragment).parentFragmentManager.navIntoHistorically(contentId(), fragmentTag, backStackTag, fragmentFactory)
}

inline fun NavigationRoot.navInto(fragmentTag: String, crossinline fragmentFactory: (() -> Fragment)) {
  (this as Fragment).parentFragmentManager.navInto(contentId(), fragmentTag, fragmentFactory)
}
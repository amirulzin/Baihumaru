package commons.android.core.fragment

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

const val FRAGMENT_ARGS = "fragment_args"

inline fun <reified T : Fragment> newInstance(args: Parcelable? = null, crossinline creator: () -> T): T {
  val target = creator()
  args?.apply {
    target.arguments = Bundle().apply {
      putParcelable(FRAGMENT_ARGS, args)
    }
  }
  return target
}

fun <T : Fragment, P : Parcelable?> T.getParcelableArguments(): P? {
  return arguments?.takeIf { it.containsKey(FRAGMENT_ARGS) }?.getParcelable<P>(FRAGMENT_ARGS)
}
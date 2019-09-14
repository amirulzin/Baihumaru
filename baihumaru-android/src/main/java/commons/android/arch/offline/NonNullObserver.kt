package commons.android.arch.offline

import androidx.lifecycle.Observer

interface NonNullObserver<T : Any> : Observer<T?> {

  override fun onChanged(t: T?) {
    t?.let(::onValueChanged)
  }

  fun onValueChanged(t: T)
}
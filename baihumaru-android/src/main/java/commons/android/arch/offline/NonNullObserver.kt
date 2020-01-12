package commons.android.arch.offline

import androidx.lifecycle.Observer

interface NonNullObserver<T : Any> : Observer<T?> {

  override fun onChanged(data: T?) {
    data?.let(::onValueChanged)
  }

  fun onValueChanged(data: T)
}
package commons.android.arch

import android.os.Bundle

interface UIConstruct<T> {
  fun init(savedInstanceState: Bundle? = null)
  fun bindUpdates(data: T)
}
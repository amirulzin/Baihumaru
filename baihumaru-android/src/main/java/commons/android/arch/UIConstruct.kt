package commons.android.arch

import android.os.Bundle
import commons.android.arch.annotations.ViewLayer

@ViewLayer
interface UIConstruct<T> {
  fun init(savedInstanceState: Bundle? = null)
  fun bindUpdates(data: T)
}
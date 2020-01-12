package commons.android.arch

import android.os.Bundle
import commons.android.arch.annotations.ViewLayer

@ViewLayer
interface UICompositeConstruct {
  fun init(savedInstanceState: Bundle? = null)
}
package commons.android

import commons.android.dagger.compat.DaggerActivity
import dagger.android.support.DaggerAppCompatDialogFragment
import dagger.android.support.DaggerFragment
import moe.baihumaru.android.BuildConfig

object ProjectConstants {
  const val DEBUG_TAG = "DEBUG_TAG"
}

typealias BuildConfigAlias = BuildConfig
typealias RootActivityAlias = DaggerActivity
typealias DialogFragmentAlias = DaggerAppCompatDialogFragment
typealias FragmentAlias = DaggerFragment
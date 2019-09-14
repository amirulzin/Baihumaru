package commons.android.dagger.compat

import android.content.Context
import androidx.preference.PreferenceFragmentCompat
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class DaggerPreferenceFragmentCompat : PreferenceFragmentCompat(), HasAndroidInjector {

  @Inject
  lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun androidInjector(): AndroidInjector<Any> {
    return childFragmentInjector
  }
}
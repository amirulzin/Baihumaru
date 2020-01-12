package moe.baihumaru.android.ui.home.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import commons.android.core.navigation.navInto
import moe.baihumaru.android.databinding.HomeNavFragmentBinding
import moe.baihumaru.android.navigation.PrimaryNavRoot
import moe.baihumaru.android.ui.defaults.CoreParentFragment

abstract class HomeNavigationFragment<F : Fragment> : CoreParentFragment<HomeNavFragmentBinding>(), PrimaryNavRoot {
  companion object {
    const val DYNAMIC_TAG = "dynamic_tag"
  }

  abstract fun initialFragmentConstructor(): F

  fun applyNewInstanceArgs(tag: String) = apply {
    arguments = (arguments ?: Bundle()).apply {
      putString(DYNAMIC_TAG, tag)
    }
  }

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): HomeNavFragmentBinding {
    return HomeNavFragmentBinding.inflate(inflater, container, false)
  }

  fun fragmentTag(): String = arguments?.getString(DYNAMIC_TAG)
    ?: throw IllegalStateException("${javaClass.simpleName} argument $DYNAMIC_TAG is null")

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) {
      childFragmentManager.navInto(binding.subContent.id, fragmentTag(), ::initialFragmentConstructor)
    }
  }
}
package moe.baihumaru.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import commons.android.core.navigation.navInto
import commons.android.viewbinding.ViewBindingFragment
import moe.baihumaru.android.databinding.HomeNavFragmentBinding
import moe.baihumaru.android.navigation.PrimaryNavRoot

abstract class HomeNavigationFragment<F : Fragment> : ViewBindingFragment<HomeNavFragmentBinding>(), PrimaryNavRoot {
  companion object {
    const val DYNAMIC_TAG = "dynamic_tag"
  }

  abstract fun fragmentConstructor(): F

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) {
      childFragmentManager.navInto(binding.subContent.id, fragmentTag(), ::fragmentConstructor)
    }
  }
}
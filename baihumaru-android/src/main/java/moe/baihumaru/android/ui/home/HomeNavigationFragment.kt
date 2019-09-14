package moe.baihumaru.android.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import commons.android.core.fragment.DataBindingFragmentCompat
import commons.android.core.navigation.navInto
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.HomeNavFragmentBinding
import moe.baihumaru.android.navigation.PrimaryNavRoot

abstract class HomeNavigationFragment<F : Fragment> : DataBindingFragmentCompat<HomeNavFragmentBinding>(), PrimaryNavRoot {
  companion object {
    const val DYNAMIC_TAG = "dynamic_tag"
  }

  abstract fun fragmentConstructor(): F

  fun applyNewInstanceArgs(tag: String) = apply {
    arguments = (arguments ?: Bundle()).apply {
      putString(DYNAMIC_TAG, tag)
    }
  }

  fun fragmentTag(): String = arguments!!.getString(DYNAMIC_TAG)!!

  override val layoutId = R.layout.home_nav_fragment

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if (savedInstanceState == null) {
      childFragmentManager.navInto(binding.subContent.id, fragmentTag(), ::fragmentConstructor)
    }
  }
}
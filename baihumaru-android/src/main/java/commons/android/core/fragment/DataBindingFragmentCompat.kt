package commons.android.core.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * Abstract fragment with ViewDataBinding helper
 */
abstract class DataBindingFragmentCompat<V : ViewDataBinding> : Fragment() {
  protected lateinit var binding: V
  protected abstract val layoutId: Int

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
    return binding.root
  }
}
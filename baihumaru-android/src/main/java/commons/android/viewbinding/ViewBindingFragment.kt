package commons.android.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import commons.android.FragmentAlias

abstract class ViewBindingFragment<V : ViewBinding> : FragmentAlias() {
  protected lateinit var binding: V
  protected abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): V
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = inflateBinding(inflater, requireNotNull(container, { "Missing parent container on this fragment: ${this::class.qualifiedName}" }))
    return binding.root
  }
}
package commons.android.core.activity

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import commons.android.RootActivityAlias

/**
 * Base DataBindingActivity. Invoke [bindContentView] to set the view contents
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class DataBindingActivity<V : ViewDataBinding> : RootActivityAlias() {
  protected lateinit var binding: V
  protected abstract val layoutId: Int

  protected fun bindContentView(): V {
    binding = DataBindingUtil.setContentView(this, layoutId)
    return binding
  }
}
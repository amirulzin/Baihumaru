package moe.baihumaru.android.ui.plugins

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.core.fragment.DataBindingFragment
import commons.android.core.recycler.SingleTypedDataBindingListAdapter
import commons.android.core.recycler.TypedDataBindingViewHolder
import commons.android.dagger.arch.DaggerViewModelFactory
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.PluginsFragmentBinding
import moe.baihumaru.android.databinding.PluginsItemBinding
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.core.Plugin
import javax.inject.Inject

class PluginsFragment : DataBindingFragment<PluginsFragmentBinding>() {
  companion object {
    const val TAG = "plugins"
    @JvmStatic
    fun newInstance() = PluginsFragment()
  }

  override val layoutId = R.layout.plugins_fragment
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    PluginConstruct(
      origin = this,
      binding = binding,
      vm = ViewModelProvider(this, vmf).get(PluginsViewModel::class.java)
    ).init(savedInstanceState)
  }

  @Inject
  lateinit var vmf: DaggerViewModelFactory<PluginsViewModel>

}


class PluginConstruct(
  private val origin: Fragment,
  private val binding: PluginsFragmentBinding,
  private val vm: PluginsViewModel
) : UIConstruct<UIPlugins> {
  override fun init(savedInstanceState: Bundle?) {
    with(binding.recyclerView) {
      adapter = PluginAdapter()
    }
    with(binding.refreshLayout) {
      bindRefresh(origin.viewLifecycleOwner, vm.pluginsLive)
    }
    vm.pluginsLive.observeNonNull(origin.viewLifecycleOwner, ::bindUpdates)
  }

  override fun bindUpdates(data: UIPlugins) {
    (binding.recyclerView.adapter as? PluginAdapter)?.submitList(data.list)
  }
}


class PluginAdapter : SingleTypedDataBindingListAdapter<UIPlugin, PluginsItemBinding>(DiffCallback()) {
  override val layoutId = R.layout.plugins_item

  override fun create(binding: PluginsItemBinding): TypedDataBindingViewHolder<UIPlugin, PluginsItemBinding> {
    return Holder(binding)
  }

  private class DiffCallback : DiffUtil.ItemCallback<UIPlugin>() {
    override fun areItemsTheSame(oldItem: UIPlugin, newItem: UIPlugin): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UIPlugin, newItem: UIPlugin): Boolean {
      return oldItem == newItem
    }
  }

  private class Holder(binding: PluginsItemBinding) : TypedDataBindingViewHolder<UIPlugin, PluginsItemBinding>(binding) {
    override fun bind(data: UIPlugin, position: Int) {
      binding.title.text = data.displayName
    }
  }
}

class PluginsViewModel @Inject constructor(val pluginsLive: PluginsLive) : RxViewModel(pluginsLive.disposables)

class PluginsLive @Inject constructor(
  private val pluginManager: PluginManager,
  errorHandler: RetrofitRxErrorHandler
) : AutoRefreshLiveData<UIPlugins>(errorHandler) {
  override fun refresh() {
    super.refresh()
    Single.fromCallable { pluginManager.reload() }
      .map { pluginManager.pluginList().map(::UIPlugin) }
      .map(::UIPlugins)
      .subscribeOn(Schedulers.computation())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}

data class UIPlugins(val list: List<UIPlugin>)

data class UIPlugin(val id: String, val displayName: String, val homeUrl: String) {
  constructor(plugin: Plugin) : this(plugin.id, plugin.displayName, plugin.homeUrl)
}
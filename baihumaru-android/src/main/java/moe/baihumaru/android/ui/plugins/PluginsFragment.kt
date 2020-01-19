package moe.baihumaru.android.ui.plugins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.arch.annotations.ViewLayer
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.viewbinding.recycler.SingleTypedViewBindingListAdapter
import commons.android.viewbinding.recycler.TypedViewBindingViewHolder
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.PluginsFragmentBinding
import moe.baihumaru.android.databinding.PluginsItemBinding
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.defaults.CoreNestedFragment
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.core.Plugin
import javax.inject.Inject

class PluginsFragment : CoreNestedFragment<PluginsFragmentBinding>() {
  companion object {
    const val TAG = "plugins"
    @JvmStatic
    fun newInstance() = PluginsFragment()
  }

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

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): PluginsFragmentBinding {
    return PluginsFragmentBinding.inflate(inflater, container, false)
  }

  override val contextualTitle by lazy { getString(R.string.nav_plugins) }
}

@ViewLayer
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


class PluginAdapter : SingleTypedViewBindingListAdapter<UIPlugin, PluginAdapter.Holder>(DiffCallback()) {

  private class DiffCallback : DiffUtil.ItemCallback<UIPlugin>() {
    override fun areItemsTheSame(oldItem: UIPlugin, newItem: UIPlugin): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UIPlugin, newItem: UIPlugin): Boolean {
      return oldItem == newItem
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(PluginsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  class Holder(binding: PluginsItemBinding) : TypedViewBindingViewHolder<UIPlugin, PluginsItemBinding>(binding) {
    override fun bind(item: UIPlugin, position: Int) {
      binding.title.text = item.displayName
    }
  }
}

class PluginsViewModel @Inject constructor(val pluginsLive: PluginsLive) : RxMultiViewModel(pluginsLive.disposables)

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
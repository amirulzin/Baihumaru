package moe.baihumaru.android.ui.catalogues

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.arch.annotations.ViewLayer
import commons.android.core.navigation.navIntoHistorically
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.viewbinding.recycler.SingleTypedViewBindingListAdapter
import commons.android.viewbinding.recycler.TypedViewBindingViewHolder
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.CataloguesFragmentBinding
import moe.baihumaru.android.databinding.CataloguesItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.defaults.CoreNestedFragment
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.android.ui.novels.NovelsFragment
import moe.baihumaru.core.Plugin
import javax.inject.Inject

class CataloguesFragment : CoreNestedFragment<CataloguesFragmentBinding>(), SubNavRoot {

  companion object {
    @JvmStatic
    fun newInstance() = CataloguesFragment()
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    CataloguesConstruct(
      origin = this,
      binding = binding,
      vm = ViewModelProvider(this, vmf).get(CataloguesViewModel::class.java)
    ).init(savedInstanceState)
  }

  @Inject
  lateinit var vmf: DaggerViewModelFactory<CataloguesViewModel>

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): CataloguesFragmentBinding {
    return CataloguesFragmentBinding.inflate(inflater, container, false)
  }

  override val contextualTitle by lazy { getString(R.string.nav_catalogues) }
}

data class UICatalogue(val id: String, val name: String) {
  constructor(plugin: Plugin) : this(plugin.id, plugin.displayName)
}

data class UICatalogues(val list: List<UICatalogue>)

@ViewLayer
class CataloguesConstruct(
  private val origin: CataloguesFragment,
  private val binding: CataloguesFragmentBinding,
  private val vm: CataloguesViewModel
) : UIConstruct<UICatalogues> {
  private val itemDelegate = object : CataloguesAdapter.ItemDelegate {
    override fun onItemClick(data: UICatalogue) {
      origin.navIntoHistorically(NovelsFragment.TAG) { NovelsFragment.newInstance(data.id, data.name) }
    }
  }

  override fun init(savedInstanceState: Bundle?) {
    with(binding.recyclerView) {
      adapter = CataloguesAdapter(itemDelegate)
    }

    with(binding.refreshLayout) {
      bindRefresh(origin.viewLifecycleOwner, vm.cataloguesLive)
    }

    vm.cataloguesLive.observeNonNull(origin.viewLifecycleOwner, ::bindUpdates)
  }

  override fun bindUpdates(data: UICatalogues) {
    (binding.recyclerView.adapter as? CataloguesAdapter)?.submitList(data.list)
  }
}

class CataloguesAdapter(private val itemDelegate: ItemDelegate) : SingleTypedViewBindingListAdapter<UICatalogue, CataloguesAdapter.Holder>(DiffCallback()) {
  class DiffCallback : DiffUtil.ItemCallback<UICatalogue>() {
    override fun areItemsTheSame(oldItem: UICatalogue, newItem: UICatalogue): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UICatalogue, newItem: UICatalogue): Boolean {
      return oldItem == newItem
    }
  }

  class Holder(binding: CataloguesItemBinding, private val delegate: ItemDelegate) : TypedViewBindingViewHolder<UICatalogue, CataloguesItemBinding>(binding) {
    override fun bind(item: UICatalogue, position: Int) {
      binding.title.text = item.name
      binding.root.setOnClickListener { delegate.onItemClick(item) }
    }
  }

  interface ItemDelegate {
    fun onItemClick(data: UICatalogue)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(CataloguesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemDelegate)
  }
}

class CataloguesViewModel @Inject constructor(val cataloguesLive: CataloguesLive) : RxMultiViewModel(cataloguesLive.disposables)

class CataloguesLive @Inject constructor(private val pluginManager: PluginManager, errorHandler: RetrofitRxErrorHandler) : AutoRefreshLiveData<UICatalogues>(errorHandler) {
  override fun refresh() {
    super.refresh()
    Single.fromCallable { pluginManager.pluginList().map(::UICatalogue) }
      .map(::UICatalogues)
      .subscribeOn(Schedulers.computation())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}
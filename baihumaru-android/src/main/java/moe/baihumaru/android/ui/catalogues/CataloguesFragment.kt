package moe.baihumaru.android.ui.catalogues

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.core.fragment.DataBindingFragment
import commons.android.core.navigation.navIntoHistorically
import commons.android.core.recycler.SingleTypedDataBindingListAdapter
import commons.android.core.recycler.TypedDataBindingViewHolder
import commons.android.dagger.arch.DaggerViewModelFactory
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.CataloguesFragmentBinding
import moe.baihumaru.android.databinding.CataloguesItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.novels.NovelsFragment
import moe.baihumaru.core.Plugin
import javax.inject.Inject

class CataloguesFragment : DataBindingFragment<CataloguesFragmentBinding>(), SubNavRoot {

  companion object {
    const val TAG = "catalogues"
    @JvmStatic
    fun newInstance() = CataloguesFragment()
  }

  override val layoutId = R.layout.catalogues_fragment

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
}

data class UICatalogue(val id: String, val name: String) {
  constructor(plugin: Plugin) : this(plugin.id, plugin.displayName)
}

data class UICatalogues(val list: List<UICatalogue>)

class CataloguesConstruct(
  private val origin: CataloguesFragment,
  private val binding: CataloguesFragmentBinding,
  private val vm: CataloguesViewModel
) : UIConstruct<UICatalogues> {
  private val itemDelegate = object : CataloguesAdapter.ItemDelegate {
    override fun onItemClick(data: UICatalogue) {
      origin.navIntoHistorically(NovelsFragment.TAG) { NovelsFragment.newInstance(data.id) }
    }
  }

  override fun init(savedInstanceState: Bundle?) {
    with(binding.recyclerView) {
      adapter = CataloguesAdapter(itemDelegate)
    }

    vm.cataloguesLive.observeNonNull(origin.viewLifecycleOwner, ::bindUpdates)
  }

  override fun bindUpdates(data: UICatalogues) {
    (binding.recyclerView.adapter as? CataloguesAdapter)?.submitList(data.list)
  }
}

class CataloguesAdapter(private val itemDelegate: ItemDelegate) : SingleTypedDataBindingListAdapter<UICatalogue, CataloguesItemBinding>(DiffCallback()) {
  class DiffCallback : DiffUtil.ItemCallback<UICatalogue>() {
    override fun areItemsTheSame(oldItem: UICatalogue, newItem: UICatalogue): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UICatalogue, newItem: UICatalogue): Boolean {
      return oldItem == newItem
    }
  }

  override val layoutId = R.layout.catalogues_item

  override fun create(binding: CataloguesItemBinding): TypedDataBindingViewHolder<UICatalogue, CataloguesItemBinding> {
    return Holder(binding, itemDelegate)
  }

  class Holder(binding: CataloguesItemBinding, private val delegate: ItemDelegate) : TypedDataBindingViewHolder<UICatalogue, CataloguesItemBinding>(binding) {
    override fun bind(data: UICatalogue, position: Int) {
      binding.title.text = data.name
      binding.root.setOnClickListener { delegate.onItemClick(data) }
    }
  }

  interface ItemDelegate {
    fun onItemClick(data: UICatalogue)
  }
}

class CataloguesViewModel @Inject constructor(val cataloguesLive: CataloguesLive) : RxViewModel(cataloguesLive.disposables)

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
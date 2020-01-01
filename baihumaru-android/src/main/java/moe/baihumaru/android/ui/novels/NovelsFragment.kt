package moe.baihumaru.android.ui.novels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.core.navigation.navIntoHistorically
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.viewbinding.ViewBindingFragment
import commons.android.viewbinding.recycler.SingleTypedViewBindingListAdapter
import commons.android.viewbinding.recycler.TypedViewBindingViewHolder
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.databinding.NovelsFragmentBinding
import moe.baihumaru.android.databinding.NovelsItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.chapters.ChaptersFragment
import moe.baihumaru.android.ui.common.UINovel
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.core.PageTraveler
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class NovelsFragment : ViewBindingFragment<NovelsFragmentBinding>(), SubNavRoot {
  companion object {
    const val TAG = "novels"
    const val KEY_PLUGIN_ID = "pluginId"

    @JvmStatic
    fun newInstance(pluginId: String) = NovelsFragment().apply {
      arguments = Bundle().apply {
        putString(KEY_PLUGIN_ID, pluginId)
      }
    }
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    NovelsConstruct(
      origin = this,
      binding = binding,
      vm = ViewModelProvider(this, vmf).get(NovelsViewModel::class.java)
    ).init(savedInstanceState)
  }

  @Inject
  lateinit var vmf: DaggerViewModelFactory<NovelsViewModel>

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): NovelsFragmentBinding {
    return NovelsFragmentBinding.inflate(inflater, container, false)
  }
}

class NovelsConstruct(
  private val origin: NovelsFragment,
  private val binding: NovelsFragmentBinding,
  private val vm: NovelsViewModel
) : UIConstruct<UINovels> {
  private val itemDelegate = object : NovelsAdapter.ItemDelegate {
    override fun onItemClick(data: UINovel) {
      origin.navIntoHistorically(ChaptersFragment.TAG) { ChaptersFragment.newInstance(data) }
    }
  }

  override fun init(savedInstanceState: Bundle?) {
    with(binding.recyclerView) {
      adapter = NovelsAdapter(itemDelegate)
    }

    with(binding.refreshLayout) {
      bindRefresh(origin.viewLifecycleOwner, vm.novelsLive)
    }

    with(vm.novelsLive) {
      pluginId.set(origin.arguments!!.getString(NovelsFragment.KEY_PLUGIN_ID)!!)
      observeNonNull(origin.viewLifecycleOwner, ::bindUpdates)
    }
  }

  override fun bindUpdates(data: UINovels) {
    (binding.recyclerView.adapter as? NovelsAdapter)?.submitList(data.list)
  }
}

class NovelsAdapter(private val itemDelegate: ItemDelegate) : SingleTypedViewBindingListAdapter<UINovel, NovelsAdapter.Holder>(DiffCallback()) {
  class DiffCallback : DiffUtil.ItemCallback<UINovel>() {
    override fun areItemsTheSame(oldItem: UINovel, newItem: UINovel): Boolean {
      return oldItem.pluginId == newItem.pluginId && oldItem.novelId == newItem.novelId
    }

    override fun areContentsTheSame(oldItem: UINovel, newItem: UINovel): Boolean {
      return oldItem == newItem
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(NovelsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemDelegate)
  }

  class Holder(binding: NovelsItemBinding, private val delegate: ItemDelegate) : TypedViewBindingViewHolder<UINovel, NovelsItemBinding>(binding) {
    override fun bind(item: UINovel, position: Int) {
      binding.title.text = item.novelId.title
      binding.root.setOnClickListener { delegate.onItemClick(item) }
    }
  }

  interface ItemDelegate {
    fun onItemClick(data: UINovel)
  }
}


data class UINovels(val list: List<UINovel>)

class NovelsViewModel @Inject constructor(val novelsLive: NovelsLive) : RxViewModel(novelsLive.disposables)

class NovelsLive @Inject constructor(private val pluginManager: PluginManager, errorHandler: RetrofitRxErrorHandler) : AutoRefreshLiveData<UINovels>(errorHandler) {
  val pluginId = AtomicReference<String?>(null)
  val pageTraveler = AtomicReference<PageTraveler>(PageTraveler())

  override fun refresh() {
    super.refresh()
    Single.fromCallable {
      val targetPluginId = pluginId.get()
      if (targetPluginId != null) {
        pluginManager.retrieve(targetPluginId)
          .provideNovels(pageTraveler = pageTraveler.get())
          .map { novelId -> UINovel(targetPluginId, novelId) }
      } else {
        emptyList()
      }
    }.map(::UINovels)
      .subscribeOn(Schedulers.io())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}
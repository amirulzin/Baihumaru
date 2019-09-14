package moe.baihumaru.android.ui.novels

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

class NovelsFragment : DataBindingFragment<NovelsFragmentBinding>(), SubNavRoot {
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

  override val layoutId = R.layout.novels_fragment
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

class NovelsAdapter(private val itemDelegate: ItemDelegate) : SingleTypedDataBindingListAdapter<UINovel, NovelsItemBinding>(DiffCallback()) {
  override val layoutId = R.layout.novels_item

  override fun create(binding: NovelsItemBinding): TypedDataBindingViewHolder<UINovel, NovelsItemBinding> {
    return Holder(binding, itemDelegate)
  }

  class DiffCallback : DiffUtil.ItemCallback<UINovel>() {
    override fun areItemsTheSame(oldItem: UINovel, newItem: UINovel): Boolean {
      return oldItem.pluginId == newItem.pluginId && oldItem.novelId == newItem.novelId
    }

    override fun areContentsTheSame(oldItem: UINovel, newItem: UINovel): Boolean {
      return oldItem == newItem
    }
  }

  class Holder(binding: NovelsItemBinding, private val delegate: ItemDelegate) : TypedDataBindingViewHolder<UINovel, NovelsItemBinding>(binding) {
    override fun bind(data: UINovel, position: Int) {
      binding.title.text = data.novelId.title
      binding.root.setOnClickListener { delegate.onItemClick(data) }
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
package moe.baihumaru.android.ui.novels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.arch.annotations.ViewLayer
import commons.android.core.navigation.navIntoHistorically
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.viewbinding.recycler.SingleTypedViewBindingListAdapter
import commons.android.viewbinding.recycler.TypedViewBindingViewHolder
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.databinding.NovelsFragmentBinding
import moe.baihumaru.android.databinding.NovelsItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.chapters.ChaptersFragment
import moe.baihumaru.android.ui.common.UINovel
import moe.baihumaru.android.ui.defaults.CoreNestedFragment
import moe.baihumaru.android.ui.defaults.PageTravelerLive
import moe.baihumaru.android.ui.defaults.PluginIdLive
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.core.PageTraveler
import moe.baihumaru.core.PluginError
import javax.inject.Inject

class NovelsFragment : CoreNestedFragment<NovelsFragmentBinding>(), SubNavRoot {
  companion object {
    const val TAG = "novels"
    const val KEY_PLUGIN_ID = "pluginId"
    const val KEY_PLUGIN_NAME = "pluginName"

    @JvmStatic
    fun newInstance(pluginId: String, pluginName: String) = NovelsFragment().apply {
      arguments = Bundle().apply {
        putString(KEY_PLUGIN_ID, pluginId)
        putString(KEY_PLUGIN_NAME, pluginName)
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

  override fun contextualTitle() = requireNotNull(arguments?.getString(KEY_PLUGIN_NAME))
}

@ViewLayer
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
      bindRefresh(origin.viewLifecycleOwner, vm.novelsLive, vm.stateLive)
    }

    with(vm.novelsLive) {
      vm.pluginId.value = origin.arguments?.getString(NovelsFragment.KEY_PLUGIN_ID)
        ?: throw IllegalStateException("Argument ${NovelsFragment.KEY_PLUGIN_ID} not supplied")
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

@Suppress("MemberVisibilityCanBePrivate")
class NovelsViewModel @Inject constructor(
  val pluginId: PluginIdLive,
  val pageTraveler: PageTravelerLive,
  val stateLive: ResourceLiveData,
  private val novelsRepository: NovelsRepository
) : RxMultiViewModel(novelsRepository.disposables) {
  val novelsLive = object : AutoRefreshLiveData2<UINovels>() {
    override fun fetch() {
      if (stateLive.isNotLoading())
        novelsRepository.retrieve(pluginId.value, pageTraveler.value, this, stateLive)
    }
  }
}

class NovelsRepository @Inject constructor(private val pluginManager: PluginManager) {
  val disposables = CompositeDisposable()

  fun retrieve(pluginId: String?, pageTraveler: PageTraveler, liveData: MutableLiveData<UINovels>, stateLiveData: ResourceLiveData) {
    stateLiveData.postLoading()
    Single.fromCallable {
      if (pluginId != null) {
        pluginManager.retrieve(pluginId)
          .provideNovels(pageTraveler = pageTraveler)
          .map { novelId -> UINovel(pluginId, novelId) }
      } else {
        emptyList()
      }
    }.map(::UINovels)
      .onErrorResumeNext(::transientErrorHandling)
      .subscribeOn(Schedulers.io())
      .doOnSuccess(stateLiveData::postComplete)
      .subscribe(liveData::postValue, stateLiveData::postError)
      .addTo(disposables)
  }

  private fun transientErrorHandling(error: Throwable): Single<UINovels> {
    return Single.error(when (error) {
      is PluginError.SiteGuardException -> error  //TODO better handling of error
      else -> error
    })
  }
}
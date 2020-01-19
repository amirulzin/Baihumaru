package moe.baihumaru.android.ui.chapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.arch.annotations.ViewLayer
import commons.android.core.navigation.navIntoHistorically
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.fromParcel
import commons.android.viewbinding.recycler.SingleTypedViewBindingListAdapter
import commons.android.viewbinding.recycler.TypedViewBindingViewHolder
import commons.android.withParcel
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.databinding.ChaptersFragmentBinding
import moe.baihumaru.android.databinding.ChaptersItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.common.UIChapterId
import moe.baihumaru.android.ui.common.UINovel
import moe.baihumaru.android.ui.defaults.CoreNestedFragment
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.android.ui.webview.WebViewFragment
import moe.baihumaru.core.PageTraveler
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class ChaptersFragment : CoreNestedFragment<ChaptersFragmentBinding>(), SubNavRoot {
  companion object {
    const val TAG = "chapters"
    const val KEY_NOVEL = "novel"
    @JvmStatic
    fun newInstance(novel: UINovel) = ChaptersFragment().withParcel(KEY_NOVEL, novel)
  }

  @Inject
  lateinit var vmf: DaggerViewModelFactory<ChaptersViewModel>

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    ChaptersConstruct(
      origin = this,
      binding = binding,
      vm = viewModelOf(ChaptersViewModel::class.java, vmf)
    ).init(savedInstanceState)
  }

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): ChaptersFragmentBinding {
    return ChaptersFragmentBinding.inflate(inflater, container, false)
  }

  override val contextualTitle by lazy {
    val novel: UINovel = fromParcel(KEY_NOVEL)
    novel.novelId.title
  }
}

@ViewLayer
class ChaptersConstruct(
  private val origin: ChaptersFragment,
  private val vm: ChaptersViewModel,
  private val binding: ChaptersFragmentBinding
) : UIConstruct<UIChapters> {
  private val itemDelegate = object : ChaptersAdapter.ItemDelegate {
    override fun onChapterClick(item: UIChapterId) {
//      origin.navIntoHistorically(ReaderFragment.TAG) { ReaderFragment.newInstance(item) }
      origin.activity?.let {
        //origin.startActivity(ReaderActivity.intentFactory(it, item))
        item.url?.let { url ->
          origin.navIntoHistorically(WebViewFragment.TAG) { WebViewFragment.intentFactory(url) }
        }
      }
    }
  }

  override fun init(savedInstanceState: Bundle?) {

    with(binding.recyclerView) {
      adapter = ChaptersAdapter(itemDelegate)
    }
    with(binding.refreshLayout) {
      bindRefresh(origin.viewLifecycleOwner, vm.chaptersLive)
    }

    vm.chaptersLive.init(origin.fromParcel(ChaptersFragment.KEY_NOVEL))
    vm.chaptersLive.observeNonNull(origin.viewLifecycleOwner, ::bindUpdates)
  }

  override fun bindUpdates(data: UIChapters) {
    (binding.recyclerView.adapter as? ChaptersAdapter)?.submitList(data.list)
  }
}


class ChaptersAdapter(
  private val itemDelegate: ItemDelegate
) : SingleTypedViewBindingListAdapter<UIChapterId, ChaptersAdapter.Holder>(DiffItemCallback()) {

  private class DiffItemCallback : DiffUtil.ItemCallback<UIChapterId>() {
    override fun areItemsTheSame(oldItem: UIChapterId, newItem: UIChapterId) =
      oldItem.novel.pluginId == newItem.novel.pluginId &&
        oldItem.novel.novelId == newItem.novel.novelId &&
        oldItem.chapterId.id == newItem.chapterId.id

    override fun areContentsTheSame(oldItem: UIChapterId, newItem: UIChapterId) =
      oldItem == newItem
  }

  interface ItemDelegate {
    fun onChapterClick(item: UIChapterId)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(ChaptersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemDelegate)
  }

  class Holder(
    override val binding: ChaptersItemBinding,
    private val delegate: ItemDelegate
  ) : TypedViewBindingViewHolder<UIChapterId, ChaptersItemBinding>(binding) {
    override fun bind(item: UIChapterId, position: Int) {
      with(binding) {
        title.text = item.chapterId.title
        binding.cardRoot.setOnClickListener { delegate.onChapterClick(item) }
      }
    }
  }
}

class ChaptersViewModel @Inject constructor(val chaptersLive: ChaptersLive) : RxMultiViewModel(chaptersLive.disposables)

class ChaptersLive @Inject constructor(
  private val pluginManager: PluginManager,
  errorHandler: RetrofitRxErrorHandler
) : AutoRefreshLiveData<UIChapters>(errorHandler) {

  val novel = AtomicReference<UINovel>()
  val traveler = AtomicReference<PageTraveler>(PageTraveler())

  fun init(input: UINovel) {
    if (input != novel.get()) {
      novel.set(input)
      resetRefreshFlags()
    }
  }

  override fun refresh() {
    super.refresh()
    Single.fromCallable {
      val pluginId = novel.get().pluginId
      val novelId = novel.get().novelId
      val plugin = pluginManager.retrieve(pluginId)

      plugin.provideChapters(novelId, traveler.get())
        .map { chapterId ->
          UIChapterId(
            novel = UINovel(pluginId, novelId),
            chapterId = chapterId,
            url = plugin.provideChapterUrl(novelId, chapterId))
        }
    }.map(::UIChapters)
      .subscribeOn(Schedulers.io())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}

data class UIChapters(val list: List<UIChapterId>)


package moe.baihumaru.android.ui.chapters

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import commons.android.arch.*
import commons.android.core.fragment.DataBindingFragment
import commons.android.core.navigation.navIntoHistorically
import commons.android.core.recycler.SingleTypedDataBindingListAdapter
import commons.android.core.recycler.TypedDataBindingViewHolder
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.fromParcel
import commons.android.withParcel
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.ChaptersFragmentBinding
import moe.baihumaru.android.databinding.ChaptersItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.common.UIChapterId
import moe.baihumaru.android.ui.common.UINovel
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.android.ui.reader.ReaderFragment
import moe.baihumaru.core.PageTraveler
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class ChaptersFragment : DataBindingFragment<ChaptersFragmentBinding>(), SubNavRoot {
  companion object {
    const val TAG = "chapters"
    const val KEY_NOVEL = "novel"
    @JvmStatic
    fun newInstance(novel: UINovel) = ChaptersFragment().withParcel(KEY_NOVEL, novel)
  }

  override val layoutId = R.layout.chapters_fragment

  @Inject
  lateinit var vmf: DaggerViewModelFactory<ChaptersViewModel>

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    ChaptersConstruct(
      origin = this,
      binding = binding,
      vm = viewModelOf(vmf, ChaptersViewModel::class.java)
    ).init(savedInstanceState)
  }
}

class ChaptersConstruct(
  private val origin: ChaptersFragment,
  private val vm: ChaptersViewModel,
  private val binding: ChaptersFragmentBinding
) : UIConstruct<UIChapters> {
  private val itemDelegate = object : ChaptersAdapter.ItemDelegate {
    override fun onChapterClick(item: UIChapterId) {
      origin.navIntoHistorically(ReaderFragment.TAG) { ReaderFragment.newInstance(item) }
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
    with(binding.recyclerView.adapter as ChaptersAdapter) {
      submitList(data.list)
    }
  }
}


class ChaptersAdapter(
  private val itemDelegate: ItemDelegate
) : SingleTypedDataBindingListAdapter<UIChapterId, ChaptersItemBinding>(DiffItemCallback()) {
  override val layoutId = R.layout.chapters_item

  override fun create(binding: ChaptersItemBinding): TypedDataBindingViewHolder<UIChapterId, ChaptersItemBinding> {
    return Holder(binding, itemDelegate)
  }

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

  class Holder(
    override val binding: ChaptersItemBinding,
    private val delegate: ItemDelegate
  ) : TypedDataBindingViewHolder<UIChapterId, ChaptersItemBinding>(binding) {
    override fun bind(data: UIChapterId, position: Int) {
      with(binding) {
        title.text = data.chapterId.title
        binding.cardRoot.setOnClickListener { delegate.onChapterClick(data) }
      }
    }
  }
}

class ChaptersViewModel @Inject constructor(val chaptersLive: ChaptersLive) : RxViewModel(chaptersLive.disposables)

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
      pluginManager.retrieve(pluginId)
        .provideChapters(novelId, traveler.get())
        .map { chapterId -> UIChapterId(UINovel(pluginId, novelId), chapterId) }
    }.map(::UIChapters)
      .subscribeOn(Schedulers.io())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}

data class UIChapters(val list: List<UIChapterId>)


package moe.baihumaru.android.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.squareup.moshi.JsonClass
import commons.android.arch.*
import commons.android.arch.annotations.ViewLayer
import commons.android.core.navigation.navIntoHistorically
import commons.android.core.visibility.autoHideOnNull
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.viewbinding.recycler.SingleTypedViewBindingListAdapter
import commons.android.viewbinding.recycler.TypedViewBindingViewHolder
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.R
import moe.baihumaru.android.databinding.LibraryFragmentBinding
import moe.baihumaru.android.databinding.LibraryItemBinding
import moe.baihumaru.android.navigation.SubNavRoot
import moe.baihumaru.android.ui.chapters.ChaptersFragment
import moe.baihumaru.android.ui.common.UIChapterId
import moe.baihumaru.android.ui.common.UINovel
import moe.baihumaru.android.ui.defaults.CoreNestedFragment
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.android.ui.reader.ReaderActivity
import moe.baihumaru.core.DefaultChapterId
import moe.baihumaru.core.DefaultNovelId
import javax.inject.Inject

class LibraryFragment : CoreNestedFragment<LibraryFragmentBinding>(), SubNavRoot {
  companion object {
    const val TAG = "library"
    @JvmStatic
    fun newInstance() = LibraryFragment()
  }

  @Inject
  lateinit var vmf: DaggerViewModelFactory<LibraryViewModel>

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    LibraryConstruct(
      origin = this,
      binding = binding,
      vm = viewModelOf(LibraryViewModel::class.java, vmf)
    ).init(savedInstanceState)
  }

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): LibraryFragmentBinding {
    return LibraryFragmentBinding.inflate(inflater, container, false)
  }

  override val contextualTitle by lazy { getString(R.string.nav_library) }
}

@ViewLayer
class LibraryConstruct(
  private val origin: LibraryFragment,
  private val vm: LibraryViewModel,
  private val binding: LibraryFragmentBinding
) : UIConstruct<UILibrary> {
  private val itemDelegate = object : LibraryAdapter.ItemDelegate {
    override fun onLatestClick(item: UILibraryItem) {
      navToChapter(item.toUIChapter(item.latest!!))
    }

    override fun onCurrentClick(item: UILibraryItem) {
      navToChapter(item.toUIChapter(item.current!!))
    }

    override fun onNovelClick(item: UILibraryItem) {
      navToNovel(item.toUINovel())
    }
  }

  override fun init(savedInstanceState: Bundle?) {
    with(binding.recyclerView) {
      adapter = LibraryAdapter(itemDelegate)
    }
    with(binding.refreshLayout) {
      bindRefresh(origin.viewLifecycleOwner, vm.libraryLive)
    }

    vm.libraryLive.observeNonNull(origin.viewLifecycleOwner, ::bindUpdates)
  }

  override fun bindUpdates(data: UILibrary) {
    (binding.recyclerView.adapter as? LibraryAdapter)?.submitList(data.list)
  }

  private fun navToChapter(chapter: UIChapterId?) {
    if (chapter != null) {
      origin.activity?.let {
        origin.startActivity(ReaderActivity.intentFactory(it, chapter))
      }
//      origin.navIntoHistorically(ReaderFragment.TAG) { ReaderFragment.newInstance(chapter) }
    }
  }

  private fun navToNovel(novel: UINovel) {
    origin.navIntoHistorically(ChaptersFragment.TAG) { ChaptersFragment.newInstance(novel) }
  }
}

class LibraryAdapter(
  private val itemDelegate: ItemDelegate
) : SingleTypedViewBindingListAdapter<UILibraryItem, LibraryAdapter.Holder>(DiffItemCallback()) {


  private class DiffItemCallback : DiffUtil.ItemCallback<UILibraryItem>() {
    override fun areItemsTheSame(oldItem: UILibraryItem, newItem: UILibraryItem) =
      oldItem.pluginId == newItem.pluginId && oldItem.novelId == newItem.novelId

    override fun areContentsTheSame(oldItem: UILibraryItem, newItem: UILibraryItem) =
      oldItem == newItem
  }

  interface ItemDelegate {
    fun onLatestClick(item: UILibraryItem)
    fun onCurrentClick(item: UILibraryItem)
    fun onNovelClick(item: UILibraryItem)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
    return Holder(LibraryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemDelegate)
  }

  class Holder(
    override val binding: LibraryItemBinding,
    private val delegate: ItemDelegate
  ) : TypedViewBindingViewHolder<UILibraryItem, LibraryItemBinding>(binding) {
    override fun bind(item: UILibraryItem, position: Int) {
      with(binding) {
        title.text = item.novelId.title
        primary.autoHideOnNull(item.current) { current ->
          primary.text = current.title
          primary.setOnClickListener { delegate.onCurrentClick(item) }
        }
        secondary.autoHideOnNull(item.latest) { latest ->
          secondary.text = latest.title
          secondary.setOnClickListener { delegate.onLatestClick(item) }
        }
        binding.cardRoot.setOnClickListener { delegate.onNovelClick(item) }
      }
    }
  }
}

class LibraryViewModel @Inject constructor(val libraryLive: LibraryLive) : RxMultiViewModel(libraryLive.disposables)

class LibraryLive @Inject constructor(
  private val libraryPref: LibraryPref,
  errorHandler: RetrofitRxErrorHandler
) : AutoRefreshLiveData<UILibrary>(errorHandler) {
  override fun refresh() {
    super.refresh()
    Single.fromCallable { libraryPref.load() ?: UILibrary(emptyList()) }
      .subscribeOn(Schedulers.io())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}

@JsonClass(generateAdapter = true)
data class UILibrary(val list: List<UILibraryItem>)

@JsonClass(generateAdapter = true)
data class UILibraryItem(
  val pluginId: String,
  val novelId: DefaultNovelId,
  val current: DefaultChapterId? = null,
  val latest: DefaultChapterId? = null,
  @Transient
  val isLoading: Boolean = false
) {
  fun toUINovel(): UINovel {
    return UINovel(
      pluginId = pluginId,
      novelId = novelId
    )
  }

  fun toUIChapter(chapterId: DefaultChapterId): UIChapterId {
    return UIChapterId(toUINovel(), chapterId)
  }
}
package moe.baihumaru.android.ui.reader

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import commons.android.arch.*
import commons.android.dagger.arch.DaggerViewModelFactory
import commons.android.fromParcel
import commons.android.viewbinding.ViewBindingFragment
import commons.android.withParcel
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import moe.baihumaru.android.databinding.ReaderFragmentBinding
import moe.baihumaru.android.plugin.PluginManager
import moe.baihumaru.android.ui.common.UIChapterId
import moe.baihumaru.android.ui.defaults.bindRefresh
import moe.baihumaru.core.ChapterBehavior
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class ReaderFragment : ViewBindingFragment<ReaderFragmentBinding>() {
  companion object {
    const val TAG = "reader"
    const val KEY_CHAPTER = "chapter"
    @JvmStatic
    fun newInstance(chapter: UIChapterId) = ReaderFragment()
      .withParcel(KEY_CHAPTER, chapter)
  }


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    ReaderConstruct(
      origin = this,
      binding = binding,
      vm = ViewModelProvider(this, vmf).get(ReaderViewModel::class.java)
    ).init(savedInstanceState)
  }

  @Inject
  lateinit var vmf: DaggerViewModelFactory<ReaderViewModel>

  override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup): ReaderFragmentBinding {
    return ReaderFragmentBinding.inflate(inflater, container, false)
  }
}

class ReaderConstruct(
  private val origin: ReaderFragment,
  private val binding: ReaderFragmentBinding,
  private val vm: ReaderViewModel
) : UIConstruct<UIReader> {

  override fun init(savedInstanceState: Bundle?) {
    with(binding.refreshLayout) {
      bindRefresh(origin.viewLifecycleOwner, vm.readerLive)
    }
    with(origin) {
      vm.readerLive.run {
        init(fromParcel(ReaderFragment.KEY_CHAPTER))
        observeNonNull(viewLifecycleOwner, ::bindUpdates)
      }
    }

    with(binding.offline) {

    }
  }

  override fun bindUpdates(data: UIReader) {
    binding.textView.text = data.text
  }
}

class ReaderViewModel @Inject constructor(val readerLive: ReaderLive) : RxViewModel(readerLive.disposables)

class ReaderLive @Inject constructor(
  private val pluginManager: PluginManager,
  errorHandler: RetrofitRxErrorHandler
) : AutoRefreshLiveData<UIReader>(errorHandler) {
  private val chapter = AtomicReference<UIChapterId>()
  fun init(inputChapter: UIChapterId) {
    var changed = false
    if (inputChapter != chapter.get()) {
      chapter.set(inputChapter)
      changed = true
    }
    if (changed)
      resetRefreshFlags()
  }

  override fun refresh() {
    super.refresh()
    Single.fromCallable {
      with(chapter.get()) {
        pluginManager.retrieve(novel.pluginId)
          .provideChapter(novel.novelId, chapterId)
      }
    }.map { (chapter, behavior) ->
      val text = (behavior.takeIf(ChapterBehavior::isDisplayTextCustomized)
        ?.displayText(chapter.rawText)
        ?: chapter.rawText
        ?: throw IllegalStateException("Chapter text not found"))
      UIReader(
        text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
      )
    }.subscribeOn(Schedulers.io())
      .subscribe(::postValue, errorHandler::accept)
      .addTo(disposables)
  }
}

data class UIReader(val text: Spanned)
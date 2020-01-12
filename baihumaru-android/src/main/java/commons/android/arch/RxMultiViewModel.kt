package commons.android.arch

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class RxMultiViewModel(
  protected open vararg val disposables: CompositeDisposable = arrayOf()
) : ViewModel() {
  override fun onCleared() {
    super.onCleared()
    disposables.forEach(CompositeDisposable::clear)
  }
}
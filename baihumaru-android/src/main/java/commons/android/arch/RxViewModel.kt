package commons.android.arch

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class RxViewModel(
  val disposables: CompositeDisposable = CompositeDisposable()
) : ViewModel() {
  override fun onCleared() {
    super.onCleared()
    disposables.clear()
  }
}
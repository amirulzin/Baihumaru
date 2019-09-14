package commons.android.arch

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * MediatorLiveData that calls [CompositeDisposable.clear] when onActive is invoked
 */
abstract class RxMediatorLiveData<T : Any>(val disposables: CompositeDisposable = CompositeDisposable()) : NonNullMediatorLiveData<T>() {

  fun clearDisposables() = disposables.clear()

  fun ofDisposable(disposable: Disposable) {
    disposables.add(disposable)
  }
}
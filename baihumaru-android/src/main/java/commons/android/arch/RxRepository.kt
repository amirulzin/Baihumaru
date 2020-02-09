package commons.android.arch

import io.reactivex.disposables.CompositeDisposable

abstract class RxRepository(open val disposables: CompositeDisposable = CompositeDisposable())
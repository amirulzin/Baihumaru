package moe.baihumaru.android.ui.home.crumbs

import androidx.annotation.AnyThread
import commons.android.arch.RxViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CrumbViewModel @Inject constructor(
  val crumbsLive: CrumbsLive,
  private val crumbRoots: CrumbRoots
) : RxViewModel() {
  @AnyThread
  fun updateCurrentCrumb(parentId: Int, currentTitle: String) {
    Single.fromCallable { crumbRoots.putAndRetrieveParent(parentId, currentTitle).nodeString() }
      .subscribeOn(Schedulers.computation())
      .subscribe(crumbsLive::postValue, Throwable::printStackTrace)
      .addTo(disposables)
  }

  @AnyThread
  fun switchCrumbRoot(parentId: Int) {
    val parentCrumb = crumbRoots.retrieve(parentId)
    if (parentCrumb != null) {
      Single.fromCallable { parentCrumb.nodeString() }
        .subscribeOn(Schedulers.computation())
        .subscribe(crumbsLive::postValue, Throwable::printStackTrace)
        .addTo(disposables)
    }
  }
}
package moe.baihumaru.android.ui.home.init

import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import commons.android.arch.offline.RxResourceLiveData
import commons.android.arch.offline.State
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomeInitLive @Inject constructor(private val prefs: SharedPreferences) : RxResourceLiveData<UIHomeInit>() {
  override fun onActive() {
    super.onActive()
    if (resourceState.value.state == State.READY) {
      Single.fromCallable { loadDefaults(prefs) }
        .subscribeOn(Schedulers.io())
        .doFinally { postComplete() }
        .subscribe(::postValue)
        .addTo(disposables)
    }
  }

  companion object {
    @WorkerThread
    @JvmStatic
    private fun loadDefaults(prefs: SharedPreferences): UIHomeInit {
      return UIHomeInit(appState = AppState(
        isFirstLoad = prefs.getBoolean("isFirstLoad", true)
          .also { prefs.edit().putBoolean("isFirstLoad", false).apply() }
      ))
    }
  }
}
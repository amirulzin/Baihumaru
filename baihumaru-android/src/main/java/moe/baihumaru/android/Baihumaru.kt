package moe.baihumaru.android

import androidx.appcompat.app.AppCompatDelegate
import commons.android.rx.RxGlobalErrorHandler
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.reactivex.plugins.RxJavaPlugins

class Baihumaru : DaggerApplication() {
  override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
    DaggerBaihumaruComponent.builder()
      .bind(this)
      .build()

  override fun onCreate() {
    super.onCreate()
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    RxJavaPlugins.setErrorHandler(RxGlobalErrorHandler::handle)
  }
}
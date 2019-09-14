package moe.baihumaru.android

import commons.android.dagger.ApplicationScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import moe.baihumaru.android.ui.UIModule

@ApplicationScope
@Component(modules = [
  AndroidInjectionModule::class,
  BaihumaruModule::class,
  NetworkModule::class,
  PluginModule::class,
  UIModule::class,
  PrefsModule::class
])
interface BaihumaruComponent : AndroidInjector<Baihumaru> {

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun bind(app: Baihumaru): Builder

    fun build(): BaihumaruComponent
  }
}
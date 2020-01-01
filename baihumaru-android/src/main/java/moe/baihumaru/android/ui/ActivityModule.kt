package moe.baihumaru.android.ui

import commons.android.dagger.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector
import moe.baihumaru.android.ui.home.HomeActivity
import moe.baihumaru.android.ui.home.HomeConstructModule

@Module
interface ActivityModule {
  @ContributesAndroidInjector(modules = [HomeConstructModule::class])
  @ActivityScope
  fun home(): HomeActivity
}
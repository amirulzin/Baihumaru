package moe.baihumaru.android.ui

import commons.android.dagger.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector
import moe.baihumaru.android.ui.home.HomeActivity

@Module
interface ActivityModule {
  @ContributesAndroidInjector
  @ActivityScope
  fun home(): HomeActivity
}
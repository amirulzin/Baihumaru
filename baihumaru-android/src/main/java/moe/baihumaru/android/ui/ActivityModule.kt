package moe.baihumaru.android.ui

import commons.android.dagger.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector
import moe.baihumaru.android.ui.home.HomeActivity
import moe.baihumaru.android.ui.home.nav.HomeNavModule
import moe.baihumaru.android.ui.reader.ReaderActivity

@Module
interface ActivityModule {
  @ContributesAndroidInjector(modules = [HomeNavModule::class])
  @ActivityScope
  fun home(): HomeActivity

  @ContributesAndroidInjector
  @ActivityScope
  fun reader(): ReaderActivity
}
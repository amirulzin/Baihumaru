package moe.baihumaru.android

import android.content.Context
import commons.android.dagger.ApplicationContext
import dagger.Binds
import dagger.Module

@Module
interface BaihumaruModule {
  @Binds
  @ApplicationContext
  fun bindContext(application: Baihumaru): Context
}
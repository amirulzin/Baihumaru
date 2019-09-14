package moe.baihumaru.android

import android.content.Context
import android.content.SharedPreferences
import commons.android.dagger.ApplicationContext
import commons.android.dagger.ApplicationScope
import dagger.Module
import dagger.Provides

@Module
class PrefsModule {
  companion object {
    const val PREF_NAME = "baihumaru_pref"
  }

  @Provides
  @ApplicationScope
  fun providePrefs(@ApplicationContext context: Context): SharedPreferences {
    return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
  }
}
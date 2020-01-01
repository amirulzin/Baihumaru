package moe.baihumaru.android.ui.home

import commons.android.dagger.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HomeConstructModule {
  @ContributesAndroidInjector
  @FragmentScope
  fun libraryContainer(): HomeConstruct.NavLibraryFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun cataloguesContainer(): HomeConstruct.NavCataloguesFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun pluginsContainer(): HomeConstruct.NavPluginFragment
}

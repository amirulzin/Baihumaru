package moe.baihumaru.android.ui.home.nav

import commons.android.dagger.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface HomeNavModule {
  @ContributesAndroidInjector
  @FragmentScope
  fun libraryContainer(): NavLibraryFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun cataloguesContainer(): NavCataloguesFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun pluginsContainer(): NavPluginFragment
}

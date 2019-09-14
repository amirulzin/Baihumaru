package moe.baihumaru.android.ui

import commons.android.dagger.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector
import moe.baihumaru.android.ui.catalogues.CataloguesFragment
import moe.baihumaru.android.ui.chapters.ChaptersFragment
import moe.baihumaru.android.ui.library.LibraryFragment
import moe.baihumaru.android.ui.novels.NovelsFragment
import moe.baihumaru.android.ui.plugins.PluginsFragment
import moe.baihumaru.android.ui.reader.ReaderFragment

@Module
interface FragmentModule {
  @ContributesAndroidInjector
  @FragmentScope
  fun plugins(): PluginsFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun library(): LibraryFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun catalogues(): CataloguesFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun novels(): NovelsFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun chapters(): ChaptersFragment

  @ContributesAndroidInjector
  @FragmentScope
  fun reader(): ReaderFragment

}
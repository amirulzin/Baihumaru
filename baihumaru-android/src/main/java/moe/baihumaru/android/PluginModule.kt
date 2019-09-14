package moe.baihumaru.android

import commons.android.dagger.ApplicationScope
import dagger.Module
import dagger.Provides
import moe.baihumaru.android.plugin.PluginLoader
import moe.baihumaru.android.plugin.PluginManager
import okhttp3.OkHttpClient

@Module
class PluginModule {
  @ApplicationScope
  @Provides
  fun pluginManager(pluginLoader: PluginLoader, client: OkHttpClient): PluginManager {
    return PluginManager(pluginLoader, client)
  }
}
package moe.baihumaru.android.plugin

import androidx.annotation.WorkerThread
import commons.android.arch.offline.refresh.RefreshDelegate
import commons.android.dagger.ApplicationScope
import moe.baihumaru.core.Plugin
import moe.baihumaru.plugin.wuxiaworld.WuxiaWorldPlugin
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@WorkerThread
@ApplicationScope
class PluginManager @Inject constructor(private val pluginLoader: PluginLoader, private val client: OkHttpClient) {

  private val plugins = mutableMapOf<String, Plugin>()

  init {
    WuxiaWorldPlugin().apply {
      plugins[id] = this
    }
  }

  private val refreshDelegate = RefreshDelegate(10, TimeUnit.MINUTES)

  fun pluginList(): List<Plugin> {
    if (refreshDelegate.shouldRefresh()) {
      reload()
    }
    return plugins.values.toList()
  }

  fun retrieve(pluginId: String): Plugin {
    if (refreshDelegate.shouldRefresh()) {
      reload()
    }
    return plugins[pluginId] ?: throw IllegalStateException("Plugin [$pluginId] is not available")
  }

  fun reload() {
    pluginLoader.load().map { plugin ->
      plugins[plugin.id] = plugin
    }

    plugins.map { (_, plugin) ->
      plugin.injectOkHttpClient(client)
    }
    refreshDelegate.updateLastRefresh()
  }
}
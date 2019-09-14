package moe.baihumaru.android.plugin

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import commons.android.dagger.ApplicationContext
import dalvik.system.PathClassLoader
import moe.baihumaru.core.Plugin
import javax.inject.Inject

private typealias PluginClass = Plugin

class PluginLoader @Inject constructor(@ApplicationContext private val appContext: Context) {

  private val definedPluginName by lazy {
    appContext.applicationInfo.packageName.plus(".plugin")
  }

  private val definedPluginClassName by lazy {
    appContext.applicationInfo.packageName.plus(".plugin.class")
  }

  private fun isSupportedPlugin(pkgInfo: PackageInfo): Boolean {
    return pkgInfo.reqFeatures.orEmpty().any { it.name == definedPluginName }
  }

  fun load(): List<PluginClass> {
    return appContext.packageManager.getInstalledPackages(0)
      .filter(::isSupportedPlugin)
      .flatMap { pkgInfo -> loadPlugins(appContext, pkgInfo) }
  }

  private fun loadPlugins(context: Context, pkgInfo: PackageInfo): List<PluginClass> {
    val appInfo = context.packageManager.getApplicationInfo(pkgInfo.packageName, PackageManager.GET_META_DATA) //todo - try error
    val classLoader = PathClassLoader(appInfo.sourceDir, context.classLoader)

    return retrieveClassNames(appInfo.metaData, pkgInfo).flatMap { className ->
      Class.forName(className, false, classLoader)
        .newInstance()
        .takeIf { it is PluginClass }
        ?.let { it as PluginClass }
        .let(::listOfNotNull)
    }
  }

  private fun retrieveClassNames(metadata: Bundle, pkgInfo: PackageInfo): List<String> {
    return metadata.getString(definedPluginClassName)?.split(";")
      ?.map { sourceClassName ->
        sourceClassName.trim().let { className ->
          if (className.startsWith('.'))
            pkgInfo.packageName.plus(className)
          else className
        }
      } ?: emptyList()
  }
}


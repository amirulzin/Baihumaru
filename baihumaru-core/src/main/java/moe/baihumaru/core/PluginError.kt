package moe.baihumaru.core

sealed class PluginError : Throwable() {
  class SiteGuardException : PluginError()
  class ServerUnreachableException : PluginError()
}
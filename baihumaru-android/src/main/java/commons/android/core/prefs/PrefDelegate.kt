package commons.android.core.prefs

interface PrefDelegate<T> {
  fun load(): T?
  fun save(data: T)
}
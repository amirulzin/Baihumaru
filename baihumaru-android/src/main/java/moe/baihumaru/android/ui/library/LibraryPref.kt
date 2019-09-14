package moe.baihumaru.android.ui.library

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import commons.android.core.prefs.PrefDelegate
import javax.inject.Inject

class LibraryPref @Inject constructor(
  private val moshi: Moshi,
  private val prefs: SharedPreferences
) : PrefDelegate<UILibrary> {
  private val key = "library"
  override fun save(data: UILibrary) {
    prefs.edit()
      .putString(key, moshi.adapter(UILibrary::class.java).toJson(data))
      .apply()
  }

  override fun load(): UILibrary? {
    return prefs.getString(key, null)
      ?.let(moshi.adapter(UILibrary::class.java)::fromJson)
  }
}
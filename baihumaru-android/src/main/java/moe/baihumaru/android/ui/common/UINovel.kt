package moe.baihumaru.android.ui.common

import commons.android.AndroidParcel
import moe.baihumaru.core.DefaultNovelId
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelConverter
import org.parceler.ParcelPropertyConverter

@Parcel(Parcel.Serialization.BEAN)
data class UINovel @ParcelConstructor constructor(
  val pluginId: String,
  @ParcelPropertyConverter(Converter::class)
  val novelId: DefaultNovelId
) {
  class Converter : ParcelConverter<DefaultNovelId> {
    override fun fromParcel(parcel: AndroidParcel): DefaultNovelId {
      return DefaultNovelId(
        id = parcel.readString()!!,
        title = parcel.readString()!!,
        tags = mutableListOf<String>().apply(parcel::readStringList).toSet())
    }

    override fun toParcel(input: DefaultNovelId?, parcel: AndroidParcel) {
      input?.let {
        parcel.writeString(it.id)
        parcel.writeString(it.title)
        parcel.writeStringList(it.tags.toList())
      }
    }
  }
}



package moe.baihumaru.android.ui.common

import androidx.core.os.ParcelCompat
import commons.android.AndroidParcel
import moe.baihumaru.core.DefaultChapterId
import org.parceler.Parcel
import org.parceler.ParcelConstructor
import org.parceler.ParcelConverter
import org.parceler.ParcelPropertyConverter

@Parcel(Parcel.Serialization.BEAN)
data class UIChapterId @ParcelConstructor constructor(
  val novel: UINovel,
  @ParcelPropertyConverter(Converter::class)
  val chapterId: DefaultChapterId
) {
  class Converter : ParcelConverter<DefaultChapterId> {
    override fun fromParcel(parcel: AndroidParcel?): DefaultChapterId {
      with(parcel!!) {
        return DefaultChapterId(
          id = readString()!!,
          title = readString()!!,
          isTitleSpoiler = ParcelCompat.readBoolean(this)
        )
      }
    }

    override fun toParcel(input: DefaultChapterId?, parcel: AndroidParcel?) {
      parcel?.apply {
        input?.let {
          writeString(it.id)
          writeString(it.title)
          ParcelCompat.writeBoolean(this, it.isTitleSpoiler)
        }
      }
    }
  }
}
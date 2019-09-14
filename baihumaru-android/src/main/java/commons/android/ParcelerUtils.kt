package commons.android

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import org.parceler.Parcels

fun <T, F : Fragment> F.withParcel(key: String, parcel: T): F = apply {
  arguments = (arguments ?: Bundle()).apply {
    putParcelable(key, Parcels.wrap(parcel))
  }
}

fun <T, F : Fragment> F.fromParcel(key: String): T {
  val parcelable: Parcelable = arguments?.takeIf { it.containsKey(key) }
    ?.getParcelable(key)
    ?: throw IllegalStateException("${this::class.simpleName}: Parcel with $key is null")
  return Parcels.unwrap(parcelable)
}

typealias AndroidParcel = Parcel
package commons.android.arch

import androidx.lifecycle.MutableLiveData

open class NonNullMutableLiveData<T : Any> : MutableLiveData<T> {
  constructor() : super()

  constructor(initialValue: T) : super(initialValue)

  override fun getValue(): T {
    return requireNotNull(super.getValue()) { "Non null contract violation!" }
  }
}
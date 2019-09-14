package commons.android.arch

import androidx.lifecycle.MediatorLiveData

open class NonNullMediatorLiveData<T : Any> : MediatorLiveData<T>() {
  override fun getValue(): T {
    return super.getValue()!!
  }
}
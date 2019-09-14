package commons.android.arch

import androidx.lifecycle.MutableLiveData

open class NonNullMutableLiveData<T : Any> : MutableLiveData<T>() {
  override fun getValue(): T {
    return super.getValue()!!
  }
}
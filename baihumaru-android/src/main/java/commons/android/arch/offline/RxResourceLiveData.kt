package commons.android.arch.offline

import commons.android.arch.NonNullMediatorLiveData
import commons.android.arch.RxMediatorLiveData
import io.reactivex.disposables.CompositeDisposable

/**
 * Rx resource-based live data model
 */
open class RxResourceLiveData<T : Any>(
  disposables: CompositeDisposable = CompositeDisposable()
) : RxMediatorLiveData<T>(disposables) {

  open val resourceState = object : NonNullMediatorLiveData<ResourceState>() {
    init {
      value = ResourceState(State.READY)
    }
  }

  fun postState(state: ResourceState?) {
    resourceState.postValue(state)
  }

  fun postReady() {
    resourceState.postValue(ResourceState(State.READY))
  }

  fun postError(throwable: Throwable) {
    val out = ResourceState(State.ERROR, throwable.message ?: "Unknown error", ResourceType.ANY)
    resourceState.postValue(out)
  }

  fun postError(message: String = "", type: ResourceType = ResourceType.ANY) {
    resourceState.postValue(ResourceState(State.ERROR, message, type))
  }

  fun postLoading(message: String = "", type: ResourceType = ResourceType.ANY) {
    resourceState.postValue(ResourceState(State.LOADING, message, type))
  }

  fun postComplete(message: String = "", type: ResourceType = ResourceType.ANY) {
    resourceState.postValue(ResourceState(State.COMPLETE, message, type))
  }

  fun postCompleteLocal(message: String = "") {
    resourceState.postValue(ResourceState(State.COMPLETE, message, ResourceType.LOCAL))
  }

  fun postCompleteRemote(message: String = "") {
    resourceState.postValue(ResourceState(State.COMPLETE, message, ResourceType.REMOTE))
  }

  fun postErrorLocal(message: String = "") {
    resourceState.postValue(ResourceState(State.ERROR, message, ResourceType.LOCAL))
  }

  fun postErrorRemote(message: String = "") {
    resourceState.postValue(ResourceState(State.ERROR, message, ResourceType.REMOTE))
  }

  fun consumeState(): ResourceState {
    val out = resourceState.value
    postReady()
    return out
  }
}

package commons.android.arch

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

/**
 * ViewModelProvider extensions for support library Fragment and FragmentActivity
 */

fun <T : ViewModel> ViewModelStoreOwner.viewModelOf(clazz: Class<T>, factory: ViewModelProvider.Factory? = null) =
  if (factory == null) ViewModelProvider(this).get(clazz)
  else ViewModelProvider(this, factory).get(clazz)

class MultiActivityViewModelStore @Inject constructor() : ViewModelStore() {
  val trackedRef = AtomicInteger(0)

  fun plant(owner: AppCompatActivity) {
    owner.lifecycle.addObserver(object : LifecycleEventObserver {
      @Suppress("NON_EXHAUSTIVE_WHEN")
      override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
          Lifecycle.Event.ON_CREATE -> trackedRef.incrementAndGet()
          Lifecycle.Event.ON_DESTROY -> {
            trackedRef.decrementAndGet()
            if (!owner.isChangingConfigurations) {
              clearIfAllDead()
            }
          }
        }
      }
    })
  }

  private fun clearIfAllDead() {
    if (trackedRef.get() == 0) {
      clear()
    }
  }
}
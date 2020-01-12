package commons.android.rx

import commons.android.BuildConfigAlias

object RxGlobalErrorHandler {
  @JvmStatic
  fun handle(throwable: Throwable?) {
    if (BuildConfigAlias.DEBUG) {
      throwable?.printStackTrace()
    }
  }
}
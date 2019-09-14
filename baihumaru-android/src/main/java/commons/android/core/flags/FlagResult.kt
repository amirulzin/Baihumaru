package commons.android.core.flags

data class FlagResult(
  val applied: Boolean,
  val resultFlags: Int
) {
  companion object {
    @JvmStatic
    fun toggleFlagsBy(rule: Boolean, flags: Int, targetFlag: Int): FlagResult {
      if (rule) {
        if (FlagUtil.isNotSet(flags, targetFlag)) {
          return FlagResult(true, FlagUtil.set(flags, targetFlag))
        }
      } else {
        if (FlagUtil.isSet(flags, targetFlag)) {
          return FlagResult(true, FlagUtil.unset(flags, targetFlag))
        }
      }
      return FlagResult(false, flags)
    }
  }
}

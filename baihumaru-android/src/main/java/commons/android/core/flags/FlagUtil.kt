package commons.android.core.flags

// See this for setting flags:
// https://stackoverflow.com/questions/530077/using-bitwise-operators-on-flags
object FlagUtil {
  @JvmStatic
  fun isSet(flags: Int, targetFlag: Int): Boolean {
    return flags and targetFlag != 0
  }

  @JvmStatic
  fun isNotSet(flags: Int, targetFlag: Int): Boolean {
    return flags and targetFlag == 0
  }

  @JvmStatic
  fun set(flags: Int, targetFlag: Int): Int {
    return flags or targetFlag
  }

  @JvmStatic
  fun unset(flags: Int, targetFlag: Int): Int {
    return flags and targetFlag.inv()
  }
}

package moe.baihumaru.android.database

import androidx.room.TypeConverter

class StringSetConverter {
  companion object {
    const val DELIMITER = "{@}"
  }
  @TypeConverter
  fun into(input: Set<String>): String {
    return input.joinToString(separator = DELIMITER)
  }

  @TypeConverter
  fun from(input: String): Set<String> {
    return if (input.isBlank()) emptySet() else input.split(DELIMITER).toSet()
  }
}
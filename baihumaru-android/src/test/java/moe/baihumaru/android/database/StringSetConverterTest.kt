package moe.baihumaru.android.database

import org.junit.Test

class StringSetConverterTest {

  @Test
  fun into() {
    val into = StringSetConverter().into(setOf("one", "two", "three"))
    assert(into == "one" + StringSetConverter.DELIMITER + "two" + StringSetConverter.DELIMITER + "three")
  }

  @Test
  fun from() {
    val from = StringSetConverter().from("one" + StringSetConverter.DELIMITER + "two" + StringSetConverter.DELIMITER + "three")
    assert(from == setOf("one", "two", "three"))
  }
}
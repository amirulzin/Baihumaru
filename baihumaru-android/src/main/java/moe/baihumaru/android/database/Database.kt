package moe.baihumaru.android.database

import androidx.room.*
import androidx.room.Database
import moe.baihumaru.core.*


@Entity(primaryKeys = ["pluginId", "nid_id", "cid_id"])
@TypeConverters(StringSetConverter::class)
data class ChapterEntity(
  val pluginId: String,
  @Embedded(prefix = "nid_")
  val novelId: DefaultNovelId,
  @Embedded(prefix = "cid_")
  val chapterId: DefaultChapterId,
  @Embedded(prefix = "ch_")
  val chapter: DefaultChapter,
  @Embedded(prefix = "bh_")
  val behavior: DefaultChapterBehavior
) {
  companion object {
    fun newEntity(plugin: Plugin, novelId: NovelId, chapterId: ChapterId, chapter: Chapter, behavior: ChapterBehavior) {

    }
  }
}

@Dao
interface BaihumaruDAO

@Database(entities = [ChapterEntity::class], version = 1)
abstract class Database : RoomDatabase() {
  abstract fun baihumaruDao(): BaihumaruDAO
}
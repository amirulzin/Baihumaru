package moe.baihumaru.core

import okhttp3.OkHttpClient

interface Plugin {

  fun injectOkHttpClient(client: OkHttpClient)

  val displayName: String

  val homeUrl: String

  val id: String get() = javaClass.canonicalName

  fun features(): Set<PluginFeature> = emptySet()

  fun provideNovels(pageTraveler: PageTraveler = PageTraveler()): List<DefaultNovelId> = emptyList()

  fun provideChapters(novelId: DefaultNovelId, pageTraveler: PageTraveler = PageTraveler()): List<DefaultChapterId> = emptyList()

  fun provideChapter(novelId: DefaultNovelId, chapterId: DefaultChapterId): Pair<Chapter, ChapterBehavior>

  fun provideChapterUrl(novelId: DefaultNovelId, chapterId: DefaultChapterId): String
}
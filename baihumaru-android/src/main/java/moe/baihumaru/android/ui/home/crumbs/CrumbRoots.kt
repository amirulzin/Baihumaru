package moe.baihumaru.android.ui.home.crumbs

import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
class CrumbRoots @Inject constructor() {
  private val hashMap = hashMapOf<Int, CrumbNode>()

  fun putAndRetrieveParent(parentId: Int, crumbId: String): CrumbNode {
    return hashMap
      .getOrPut(parentId) { CrumbNode(crumbId) }
      .apply { putLast(crumbId) }
  }

  fun remove(parentId: Int, crumbId: String) {
    retrieve(parentId)?.removeAt(crumbId)
  }

  fun retrieve(parentId: Int): CrumbNode? {
    return hashMap[parentId]
  }
}
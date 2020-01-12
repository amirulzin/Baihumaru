package moe.baihumaru.android.ui.home.crumbs

data class CrumbNode(val id: String, var child: CrumbNode? = null) {
  companion object {
    private const val DELIMITER = " > "
  }

  fun nodeString(): String {
    val builder = StringBuilder()
    synchronized(this) {
      var currentNode: CrumbNode? = this
      while (currentNode != null) {
        builder.append(currentNode.id)
        currentNode = currentNode.child
        if (currentNode != null) {
          builder.append(DELIMITER)
        }
      }
      return builder.toString()
    }
  }

  fun removeAt(targetId: String) {
    require(targetId != id) { "Node id being removed is the root node id!" }
    synchronized(this) {
      var currentNode: CrumbNode? = this
      while (currentNode != null) {
        if (currentNode.child?.id == id) {
          currentNode.child = null
          break
        } else {
          currentNode = currentNode.child
        }
      }
    }
  }

  fun putLast(crumbId: String): CrumbNode {
    //Any child will be null-ed out since this is the last node
    synchronized(this) {
      if (crumbId == id) {
        child = null
        return this
      } else {
        var lastParent = this
        var currentNode: CrumbNode? = child
        while (currentNode != null) {
          if (currentNode.id == crumbId) {
            currentNode.child = null
            break
          } else {
            lastParent = currentNode
            currentNode = currentNode.child
          }
        }

        if (currentNode == null) {
          currentNode = CrumbNode(crumbId)
          lastParent.child = currentNode
        }
        return currentNode
      }
    }
  }
}
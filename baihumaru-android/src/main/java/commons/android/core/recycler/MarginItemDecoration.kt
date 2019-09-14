package commons.android.core.recycler

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

open class MarginItemDecoration(
  context: Context,
  @DimenRes horizontalMargin: Int,
  @DimenRes verticalMargin: Int,
  private val direction: Direction = Direction.VERTICAL,
  private val reversed: Boolean = false
) : RecyclerView.ItemDecoration() {

  enum class Direction {
    VERTICAL,
    HORIZONTAL
  }

  private val verticalMarginPixel: Int = context.resources.getDimensionPixelSize(verticalMargin)
  private val horizontalMarginPixel: Int = context.resources.getDimensionPixelSize(horizontalMargin)

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    val childAdapterPosition = parent.getChildAdapterPosition(view)

    if (direction == Direction.VERTICAL) {
      if (childIsFirst(childAdapterPosition)) {
        (verticalMarginPixel * 2).let {
          if (reversed) outRect.bottom = it
          else outRect.top = it
        }
      }

      if (childIsLast(childAdapterPosition, parent)) {
        (verticalMarginPixel * 2).let {
          if (reversed) outRect.top = it
          else outRect.bottom = it //double margin at bottom
        }
      } else {
        (verticalMarginPixel).let {
          if (reversed) outRect.top = it
          else outRect.bottom = it
        }
      }

      outRect.left = horizontalMarginPixel
      outRect.right = horizontalMarginPixel
    } else if (direction == Direction.HORIZONTAL) {

      if (childIsFirst(childAdapterPosition)) {
        (horizontalMarginPixel * 2).let {
          if (reversed) outRect.right = it
          else outRect.left = it
        }
      }
      if (childIsLast(childAdapterPosition, parent)) {
        (horizontalMarginPixel * 2).let {
          if (reversed) outRect.left = it
          else outRect.right = it
        }
      } else {
        horizontalMarginPixel.let {
          if (reversed) outRect.left = it
          else outRect.right = it
        }
      }

      outRect.top = verticalMarginPixel
      outRect.bottom = verticalMarginPixel
    }
  }

  private fun childIsFirst(childAdapterPosition: Int) = childAdapterPosition == 0

  private fun childIsLast(childAdapterPosition: Int, parent: RecyclerView) =
    childAdapterPosition + 1 == parent.adapter?.itemCount
}


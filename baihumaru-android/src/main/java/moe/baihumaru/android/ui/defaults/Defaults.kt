package moe.baihumaru.android.ui.defaults

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import commons.android.core.context.ContextUtils
import commons.android.core.recycler.MarginItemDecoration
import moe.baihumaru.android.R

class DefaultMarginItemDecoration(context: Context) : MarginItemDecoration(
  context = context,
  horizontalMargin = R.dimen.item_margin_horizontal,
  verticalMargin = R.dimen.item_margin_vertical
)

class DefaultSwipeRefreshLayout @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) : SwipeRefreshLayout(context, attr) {
  init {
    val isNightMode = ContextUtils.isNightMode(context)
    setProgressBackgroundColorSchemeResource(if (isNightMode) R.color.colorSurfaceDark else R.color.colorSurface)
    setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark)
  }
}

class DefaultRecyclerView @JvmOverloads constructor(
  context: Context,
  attr: AttributeSet? = null,
  defStyle: Int = 0
) : RecyclerView(context, attr, defStyle) {
  init {
    addItemDecoration(DefaultMarginItemDecoration(context))
    layoutManager = LinearLayoutManager(context)
  }
}
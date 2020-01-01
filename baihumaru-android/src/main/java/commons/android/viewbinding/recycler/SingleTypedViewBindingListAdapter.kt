package commons.android.viewbinding.recycler

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class SingleTypedViewBindingListAdapter<T, VH : TypedViewBindingViewHolder<T, ViewBinding>> : ListAdapter<T, VH> {

  constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback)

  constructor(asyncDifferConfig: AsyncDifferConfig<T>) : super(asyncDifferConfig)

  override fun onBindViewHolder(holder: VH, position: Int) {
    holder.bind(getItem(position), position)
  }
}
package commons.android.viewbinding.recycler

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class TypedViewBindingViewHolder<T, out V : ViewBinding>(open val binding: V) : RecyclerView.ViewHolder(binding.root) {
  abstract fun bind(item: T, position: Int)
}
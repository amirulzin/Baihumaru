package commons.android.core.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class TypedDataBindingViewHolder<T, out V : ViewDataBinding>(open val binding: V) : ViewHolder(binding.root) {
  abstract fun bind(data: T, position: Int)
}
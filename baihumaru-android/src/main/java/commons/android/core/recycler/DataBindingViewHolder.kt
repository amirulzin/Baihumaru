package commons.android.core.recycler

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView.ViewHolder

open class DataBindingViewHolder<out V : ViewDataBinding>(open val binding: V) : ViewHolder(binding.root)
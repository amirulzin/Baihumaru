package commons.android.core.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class SingleTypedDataBindingListAdapter<T, V : ViewDataBinding>(diffCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, TypedDataBindingViewHolder<T, V>>(diffCallback) {

  override fun onBindViewHolder(holder: TypedDataBindingViewHolder<T, V>, position: Int) {
    holder.bind(getItem(position), position)
    holder.binding.executePendingBindings()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypedDataBindingViewHolder<T, V> {
    return create(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false))
  }

  @get:LayoutRes
  protected abstract val layoutId: Int

  protected abstract fun create(binding: V): TypedDataBindingViewHolder<T, V>
}
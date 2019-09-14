package commons.android.core.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class TypedListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, TypedDataBindingViewHolder<T, *>>(diffCallback) {

  override fun onBindViewHolder(holder: TypedDataBindingViewHolder<T, *>, position: Int) {
    holder.bind(getItem(position), position)
    holder.binding.executePendingBindings()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypedDataBindingViewHolder<T, *> {
    return create(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutIdBy(viewType), parent, false))
  }

  @LayoutRes
  protected abstract fun layoutIdBy(viewType: Int): Int

  protected abstract fun create(binding: ViewDataBinding): TypedDataBindingViewHolder<T, *>
}
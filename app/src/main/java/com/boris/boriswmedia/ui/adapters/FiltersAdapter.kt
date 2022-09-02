package com.boris.boriswmedia.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boris.boriswmedia.R
import com.boris.boriswmedia.data.models.Filter
import com.boris.boriswmedia.databinding.FilterItemBinding

class FiltersAdapter(private val listener: OnItemClickListener):
    ListAdapter<Filter, FiltersAdapter.FiltersViewHolder>(FILTERS_COMPARATOR)  {

    interface OnItemClickListener {
        fun onItemClick(filter: Filter, position: Int)
    }


    override fun onBindViewHolder(holder: FiltersViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersViewHolder {
        val binding =
            FilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return FiltersViewHolder(binding)
    }


    inner class FiltersViewHolder(private val binding: FilterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item,position)
                    }
                }
            }
        }

        fun bind(filter: Filter) {
            binding.apply {
                title.text = filter.title
                when(filter.selected){
                    true -> title.setTextColor(ContextCompat.getColor(binding.title.context, R.color.teal_200))

                    false -> title.setTextColor(ContextCompat.getColor(binding.title.context, R.color.black))

                }


            }
        }
    }

    companion object {
        private val FILTERS_COMPARATOR = object : DiffUtil.ItemCallback<Filter>() {
            override fun areItemsTheSame(oldItem: Filter, newItem: Filter) =
                oldItem.selected == newItem.selected

            override fun areContentsTheSame(oldItem: Filter, newItem: Filter) =
                oldItem == newItem
        }
    }
}
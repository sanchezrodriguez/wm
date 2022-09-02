package com.boris.boriswmedia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.boris.boriswmedia.data.models.News
import com.boris.boriswmedia.R
import com.boris.boriswmedia.databinding.SingleNewsItemBinding
import com.bumptech.glide.Glide

class NewsAdapter(private val listener: OnItemClickListener):
    PagingDataAdapter<News, NewsAdapter.NewsViewHolder>(NEWS_COMPARATOR)  {

    interface OnItemClickListener {
        fun onItemClick(news: News)
    }


    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding =
            SingleNewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NewsViewHolder(binding)
    }


    inner class NewsViewHolder(private val binding: SingleNewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(news: News) {
            binding.apply {
                Glide.with(itemView)
                    .load(news.urlToImage)
                    .error(R.drawable.ic_error)
                    .into(newsImage)

                newsText.text = news.title
                author.text = binding.author.context.getString(R.string.news_from,news.source.name)
            }
        }
    }

    companion object {
        private val NEWS_COMPARATOR = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News) =
                oldItem.source.id == newItem.source.id

            override fun areContentsTheSame(oldItem: News, newItem: News) =
                oldItem == newItem
        }
    }
}
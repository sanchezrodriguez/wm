package com.boris.boriswmedia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boris.boriswmedia.databinding.NewsFooterBinding
import retrofit2.HttpException
import java.io.IOException


class NewsLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<NewsLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = NewsFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: NewsFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                if ( loadState is LoadState.Error){
                    when (loadState.error) {

                        is IOException -> {
                            binding.netError.isVisible = true
                            binding.serverError.isVisible = false;

                        }
                        is HttpException -> {
                            binding.netError.isVisible = false
                            binding.serverError.isVisible = true;                            }
                    }
                }
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading
                netError.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}
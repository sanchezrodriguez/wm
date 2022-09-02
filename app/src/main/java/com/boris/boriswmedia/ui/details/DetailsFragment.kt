package com.boris.boriswmedia.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.boris.boriswmedia.R
import com.boris.boriswmedia.databinding.FragmentDetailsBinding
import com.boris.boriswmedia.ui.main.MainActivityViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    val sharedViewModel: MainActivityViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        sharedViewModel.sharedData.observe(viewLifecycleOwner){
            binding.apply {
                Glide.with(requireContext())
                    .load(it.urlToImage)
                    .error(R.drawable.ic_error)
                    .into(detailsImage)
                sourceTextView.text = getString(R.string.news_from,it.source.name)
                detailsTextView.text = it.content
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
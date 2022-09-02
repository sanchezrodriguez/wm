package com.boris.boriswmedia.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.boris.boriswmedia.data.models.News
import com.boris.boriswmedia.R
import com.boris.boriswmedia.data.models.Filter
import com.boris.boriswmedia.databinding.MainFragmentBinding
import com.boris.boriswmedia.ui.adapters.FiltersAdapter
import com.boris.boriswmedia.ui.adapters.NewsAdapter
import com.boris.boriswmedia.ui.adapters.NewsLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_fragment.*
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.text.FieldPosition


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.main_fragment), NewsAdapter.OnItemClickListener,FiltersAdapter.OnItemClickListener {

    private val mainViewModel by viewModels<MainViewModel>()
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NewsAdapter
    private lateinit var searchView: SearchView
    private lateinit var navController: NavController
    val sharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var filtersAdapter: FiltersAdapter
    private lateinit var filters: ArrayList<Filter>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = MainFragmentBinding.bind(view)
        initialize()
        observe()
        listeners()
    }

    private fun initialize() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.news_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                searchView = menuItem.actionView as SearchView
                searchView.setQueryHint(getString(R.string.search));

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {

                        if (query != null) {
                            mainViewModel.addDefaultFilters()
                            mainViewModel.searchPhotos(query)
                            searchView.clearFocus()
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })
                searchView.setOnQueryTextFocusChangeListener { _ , hasFocus ->
                    if (hasFocus) {
                        val animationFadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
                        animationFadeOut.fillAfter = true
                        binding.recyclers.startAnimation(animationFadeOut)

                    } else {
                        val animationFadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
                        animationFadeIn.fillAfter = true
                        binding.recyclers.startAnimation(animationFadeIn)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = NewsAdapter(this)
        filtersAdapter = FiltersAdapter(this)
        binding.apply {
            recyclerMain.setHasFixedSize(true)
            recyclerMain.itemAnimator = null
            recyclerMain.adapter = adapter.withLoadStateHeaderAndFooter(
                header = NewsLoadStateAdapter { adapter.retry() },
                footer = NewsLoadStateAdapter { adapter.retry() },
            )
            recyclerMain.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            recyclerMainTop.setHasFixedSize(true)
            recyclerMainTop.itemAnimator = null
            recyclerMainTop.adapter = filtersAdapter

        }

        navController = findNavController()


    }
    private fun observe(){
        mainViewModel.news.observe(viewLifecycleOwner){
            adapter.submitData(viewLifecycleOwner.lifecycle,it)
        }
        mainViewModel.filtersList.observe(viewLifecycleOwner){
            filters = it
            filtersAdapter.submitList(null)
            filtersAdapter.submitList(it)
        }
    }

    private fun listeners(){
        adapter.addLoadStateListener { loadState ->
            binding.pullMain.isRefreshing = false
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerMain.isVisible = loadState.source.refresh is LoadState.NotLoading
                recyclerMainTop.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                internetError.isVisible = loadState.source.refresh is LoadState.Error
                serverError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1) {
                    recyclerMain.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }

                val errorState = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                when (errorState?.error) {
                    is IOException -> {
                        internetError.isVisible = true
                        serverError.isVisible = false;
                        buttonRetry.isVisible = true
                    }
                    is HttpException -> {
                        binding.internetError.isVisible = false
                        binding.serverError.isVisible = true;
                        buttonRetry.isVisible = true
                    }
                    is Exception -> {
                        binding.internetError.isVisible = false
                        binding.serverError.isVisible = true;
                        buttonRetry.isVisible = true
                    }
                }
            }

        }
        binding.pullMain.setOnRefreshListener {
            mainViewModel.addDefaultFilters()
            mainViewModel.selectedCategory = ""
            mainViewModel.searchPhotos("")
        }


        binding.buttonRetry.setOnClickListener {
            adapter.retry()
        }
    }

    override fun onItemClick(news: News) {
        sharedViewModel.sharedData.postValue(news)
        navController.navigate(R.id.action_main_fragment_to_details_fragment2)
    }



    override fun onItemClick(filter: Filter,position: Int) {

        if(filter.selected){
            filter.selected = false
            mainViewModel.selectedCategory = ""
        } else{
            filters.forEach{
                it.selected = false
            }
            filter.selected = true
            mainViewModel.selectedCategory = filter.title
        }

        filters.set(position,filter)
        mainViewModel.searchPhotos("")
        mainViewModel.filtersList.postValue(filters)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
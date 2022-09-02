package com.boris.boriswmedia.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.boris.boriswmedia.data.models.Filter
import com.boris.boriswmedia.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    var filtersList: MutableLiveData<ArrayList<Filter>> = MutableLiveData()
    var selectedCategory: String = ""

    companion object {
        private const val DEFAULT_QUERY = ""
    }

    init {
       addDefaultFilters()
    }

    private val currentQuery = MutableLiveData(DEFAULT_QUERY)

    val news = currentQuery.switchMap { queryString ->
        repository.getSearchResults(queryString,selectedCategory).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    fun addDefaultFilters(){
        val filterList: ArrayList<Filter> = ArrayList()
        filterList.add(Filter("business",false))
        filterList.add(Filter("sports",false))
        filterList.add(Filter("politics",false))
        filterList.add(Filter("covid",false))
        filtersList.postValue(filterList)
    }
}
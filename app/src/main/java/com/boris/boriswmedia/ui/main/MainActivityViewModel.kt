package com.boris.boriswmedia.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boris.boriswmedia.data.models.News
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel  @Inject constructor(): ViewModel() {
    var sharedData: MutableLiveData<News> = MutableLiveData()
}
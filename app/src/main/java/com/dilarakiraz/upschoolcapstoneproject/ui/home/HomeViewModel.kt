package com.dilarakiraz.upschoolcapstoneproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel():ViewModel(){

    private var _mainState = MutableLiveData<HomeState>()
    val mainState : LiveData<HomeState>
        get() = _mainState


}

sealed interface HomeState{
    object Loading : HomeState
    data class EmptyScreen(val message : String) : HomeState
    data class Error(val throwable: Throwable) :HomeState
}
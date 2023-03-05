package com.example.weatherapp.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.Repository

class HomeViewModelFactory( private val repo : Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            HomeViewModel(repo) as T
        }
        else{
            throw java.lang.IllegalArgumentException("View modle class not found")
        }
    }


}
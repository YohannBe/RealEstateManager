package com.example.realestatemanager.viewmodel.profileFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.realestatemanager.model.repositories.RealEstateAgentRepository
import com.example.realestatemanager.model.repositories.RealEstateRepository
import java.util.concurrent.Executor

class ProfileFragmentViewModelFactory(private val realEstateAgentRepository: RealEstateAgentRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileFragmentViewModel(realEstateAgentRepository) as T
    }
}